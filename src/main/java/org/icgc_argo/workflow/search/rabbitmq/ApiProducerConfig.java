/*
 * Copyright (c) 2020 The Ontario Institute for Cancer Research. All rights reserved
 *
 * This program and the accompanying materials are made available under the terms of the GNU Affero General Public License v3.0.
 * You should have received a copy of the GNU Affero General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED
 * TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
 * ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.icgc_argo.workflow.search.rabbitmq;

import static org.icgc_argo.workflow.search.util.JacksonUtils.toJsonString;

import com.pivotal.rabbitmq.RabbitEndpointService;
import com.pivotal.rabbitmq.source.OnDemandSource;
import com.pivotal.rabbitmq.source.Sender;
import com.pivotal.rabbitmq.stream.TransactionalProducerStream;
import com.pivotal.rabbitmq.topology.ExchangeType;
import java.time.Instant;
import java.util.function.Function;
import javax.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.icgc_argo.workflow.search.config.rabbitmq.RabbitSchemaConfig;
import org.icgc_argo.workflow.search.model.common.RunRequest;
import org.icgc_argo.workflow.search.rabbitmq.schema.EngineParams;
import org.icgc_argo.workflow.search.rabbitmq.schema.RunState;
import org.icgc_argo.workflow.search.rabbitmq.schema.WfMgmtRunMsg;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.Disposable;

@Slf4j
@AutoConfigureAfter(RabbitSchemaConfig.class)
@Configuration
@RequiredArgsConstructor
public class ApiProducerConfig {
  @Value("${api.producer.topicExchange}")
  private String topicExchangeName;

  @Value("${api.producer.initializeRunReq}")
  private Boolean initializeRunReq;

  @Value("${trigger.producer.topicExchange}")
  public String exchange;

  @Value("${trigger.producer.routingKey}")
  public String routingKey;

  private final RabbitEndpointService rabbit;

  private final OnDemandSource<SenderDTO> sink = new OnDemandSource<>("apiSource");

  private final OnDemandSource<String> triggerSink = new OnDemandSource<>("triggerSource");

  private Disposable apiProducer;

  private Disposable triggerProducer;

  @PostConstruct
  public void init() {
    this.apiProducer = createWfMgmtRunMsgProducer();
    this.triggerProducer = createTriggerRunMsgProducer();
  }

  private Disposable createWfMgmtRunMsgProducer() {
    return createTransProducerStream(rabbit, topicExchangeName)
        .send(
            sink.source()
                .map(
                    tx -> {
                      val senderDto = tx.get();
                      if (senderDto.isCancelRequest()) {
                        return tx.map(createWfMgmtRunMsg(senderDto.getRunId(), RunState.CANCELING));
                      } else {
                        return tx.map(
                            createWfMgmtRunMsg(
                                senderDto.getRunId(),
                                senderDto.getRunRequest(),
                                initializeRunReq ? RunState.INITIALIZING : RunState.QUEUED));
                      }
                    }))
        .subscribe(
            tx -> {
              log.debug("ApiProducer sent WfMgmtRunMsg: {}", tx.get());
              tx.commit();
            });
  }

  private Disposable createTriggerRunMsgProducer() {
    return createTriggerProducerStream(rabbit, exchange).send(triggerSink.source()).subscribe();
  }

  @Bean
  public Sender<SenderDTO> sender() {
    return sink;
  }

  @Bean
  public Sender<String> triggerSender() {
    return triggerSink;
  }

  public static TransactionalProducerStream<WfMgmtRunMsg> createTransProducerStream(
      RabbitEndpointService rabbit, String topicName) {

    return rabbit
        .declareTopology(
            topologyBuilder -> topologyBuilder.declareExchange(topicName).type(ExchangeType.topic))
        .createTransactionalProducerStream(WfMgmtRunMsg.class)
        .route()
        .toExchange(topicName)
        .withRoutingKey(routingKeySelector())
        .then();
  }

  public static TransactionalProducerStream<String> createTriggerProducerStream(
      RabbitEndpointService rabbit, String topicName) {

    return rabbit
        .declareTopology(
            topologyBuilder -> topologyBuilder.declareExchange(topicName).type(ExchangeType.topic))
        .createTransactionalProducerStream(String.class)
        .route()
        .toExchange(topicName)
        .withRoutingKey(routingTriggerKeySelector())
        .then();
  }

  private static Function<WfMgmtRunMsg, String> routingKeySelector() {
    return msg -> msg.getState().toString();
  }

  private static Function<String, String> routingTriggerKeySelector() {
    return msg -> "trigger-routingKey";
  }

  private static WfMgmtRunMsg createWfMgmtRunMsg(
      String runId, RunRequest runRequest, RunState state) {

    val requestWep = runRequest.getWorkflowEngineParams();
    val msgWep =
        EngineParams.newBuilder()
            .setLatest(requestWep.getLatest())
            .setDefaultContainer(requestWep.getDefaultContainer())
            .setLaunchDir(requestWep.getLaunchDir())
            .setRevision(requestWep.getRevision())
            .setProjectDir(requestWep.getProjectDir())
            .setWorkDir(requestWep.getWorkDir());

    if (requestWep.getResume() != null) {
      msgWep.setResume(requestWep.getResume().toString());
    }
    return WfMgmtRunMsg.newBuilder()
        .setRunId(runId)
        .setState(state)
        .setWorkflowUrl(runRequest.getWorkflowUrl())
        .setWorkflowParamsJsonStr(toJsonString(runRequest.getWorkflowParams()))
        .setWorkflowEngineParams(msgWep.build())
        .setTimestamp(Instant.now().toEpochMilli())
        .build();
  }

  private static WfMgmtRunMsg createWfMgmtRunMsg(String runId, RunState state) {
    return WfMgmtRunMsg.newBuilder()
        .setRunId(runId)
        .setTimestamp(Instant.now().toEpochMilli())
        .setState(state)
        .setWorkflowEngineParams(EngineParams.newBuilder().build())
        .build();
  }
}
