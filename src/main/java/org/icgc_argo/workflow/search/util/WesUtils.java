/*
 * Copyright (c) 2021 The Ontario Institute for Cancer Research. All rights reserved
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

package org.icgc_argo.workflow.search.util;

import static java.lang.String.format;

import java.util.UUID;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WesUtils {
  private static final String WES_PREFIX = "wes-";
  // run name (used for paramsFile as well)
  // You may be asking yourself, why is he replacing the "-" in the UUID, this is a valid
  // question, well unfortunately when trying to resume a job, Nextflow searches for the
  // UUID format ANYWHERE in the resume string, resulting in the incorrect assumption
  // that we are passing an runId when in fact we are passing a runName ...
  // thanks Nextflow ... this workaround solves that problem

  // UPDATE: The glory of Nextflow knows no bounds ... resuming by runName while possible
  // ends up reusing the run/session (yeah these are the same but still recorded separately) id
  // from the "last" run ... wtv run that was ... resulting in multiple resumed runs sharing the
  // same sessionId (we're going with this label) even though they have nothing to do with one
  // another. This is a bug in NF and warrants a PR but for now we recommend only resuming runs
  // with sessionId and never with runName
  public static String generateWesRunId() {
    return format("%s%s", WES_PREFIX, UUID.randomUUID().toString().replace("-", ""));
  }

  public static Boolean isValidWesRunName(String runName) {
    return runName != null && runName.startsWith(WES_PREFIX) && runName.split("-").length == 2;
  }
}
