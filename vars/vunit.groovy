def call(Map cfg = [:]) {
  def sim = cfg.get('sim', [:])
  def pattern = sim.get('test_pattern', '*')
  echo "Running VUnit tests with pattern: ${pattern}"
  sh label: 'Run VUnit (placeholder)', script: '''
set -e
echo "[vunit] Placeholder run; integrate your VUnit command here"
# Example:
# python3 -m vunit.run --xunit xml --output build/vunit --test ${pattern}
'''
  // Publish JUnit if produced by the project
  junit allowEmptyResults: true, testResults: 'build/**/junit*.xml, build/**/xunit*.xml'
  archiveArtifacts allowEmptyArchive: true, artifacts: 'build/**/waves/**, build/**/logs/**'
}

