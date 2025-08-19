def call(Map cfg = [:]) {
  def build = cfg.get('build', [:])
  def top = build.get('top', '')
  def part = build.get('part', '')
  def xdc = (build.get('constraints', []) as List<String>).join(',')
  def wnsMin = build.get('timing', [:]).get('wns_min_ns', 0.0)

  if (!top || !part) {
    error 'build.top and build.part must be set in config'
  }

  echo "Vivado build for top=${top}, part=${part}"

  def tcl = libraryResource 'xilinx/vivado_build.tcl'
  writeFile file: 'ci_vivado_build.tcl', text: tcl

  sh label: 'Vivado batch build', script: """
set -e
mkdir -p build/xilinx
vivado -mode batch -nojournal -nolog -notrace -source ci_vivado_build.tcl -- \
  --top ${top} --part ${part} --xdc "${xdc}" --outdir build/xilinx
"""

  // Try parse timing if report exists
  def parser = libraryResource 'scripts/parse_timing.py'
  writeFile file: 'parse_timing.py', text: parser
  sh label: 'Parse timing', script: """
set -e
python3 parse_timing.py build/xilinx/timing_summary.rpt > build/xilinx/timing.json || echo '{}' > build/xilinx/timing.json
cat build/xilinx/timing.json
"""

  archiveArtifacts allowEmptyArchive: true, artifacts: 'build/xilinx/**'

  // Enforce timing threshold if JSON contains WNS
  def timingJson = readJSON file: 'build/xilinx/timing.json'
  def wns = timingJson.get('wns', null)
  if (wns != null && wns < wnsMin) {
    error "Timing check failed: WNS ${wns} < min ${wnsMin} ns"
  }
}

