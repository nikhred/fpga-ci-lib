def call(Map cfg = [:]) {
  def tftp = cfg.get('deploy', [:]).get('tftp', [:])
  if (!tftp.get('enabled', false)) {
    echo 'TFTP deploy disabled; skipping'
    return
  }

  def host = tftp.get('host')
  def root = tftp.get('root')
  def user = tftp.get('user', 'tftp')
  def subdir = tftp.get('subdir', env.GIT_COMMIT ?: 'unknown')
  def updateLatest = tftp.get('update_latest', true)

  if (!host || !root) {
    error 'deploy.tftp.host and deploy.tftp.root must be set'
  }

  def dest = "${root}/${subdir}"
  echo "Deploying artifacts to ${user}@${host}:${dest}"

  withCredentials([sshUserPrivateKey(credentialsId: 'tftp-ssh', keyFileVariable: 'KEY', usernameVariable: 'SSH_USER')]) {
    sh label: 'Upload to TFTP host', script: """
set -e
rsync -e "ssh -i $KEY -o StrictHostKeyChecking=no" -av build/xilinx/ ${user}@${host}:${dest}/
"""
    if (updateLatest) {
      sh label: 'Update latest symlink', script: """
ssh -i $KEY -o StrictHostKeyChecking=no ${user}@${host} \
  'mkdir -p ${root} && cd ${root} && ln -sfn ${subdir} latest'
"""
    }
  }

  def manifest = libraryResource 'scripts/write_manifest.py'
  writeFile file: 'write_manifest.py', text: manifest
  sh label: 'Write manifest', script: """
python3 write_manifest.py \
  --out build/xilinx/manifest.json \
  --project '${cfg.get('project', 'unknown')}' \
  --sha '${env.GIT_COMMIT ?: 'unknown'}' \
  --build '${env.BUILD_NUMBER ?: '0'}'
"""

  archiveArtifacts allowEmptyArchive: true, artifacts: 'build/xilinx/manifest.json'
}

