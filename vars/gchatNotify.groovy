def call(Map cfg = [:], String status = 'UNKNOWN') {
  def gcfg = cfg.get('notify', [:]).get('google_chat', [:])
  if (!gcfg.get('enabled', false)) {
    echo 'Google Chat notify disabled; skipping'
    return
  }

  def cred = gcfg.get('credential_id', 'gchat-webhook')
  def jobUrl = env.BUILD_URL ?: ''
  def project = cfg.get('project', env.JOB_NAME ?: 'fpga-ci')

  def message = [
    text: "${project}: ${status} — Build #${env.BUILD_NUMBER} ${jobUrl}"
  ]

  withCredentials([string(credentialsId: cred, variable: 'GCHAT_WEBHOOK')]) {
    writeJSON file: 'gchat_msg.json', json: message
    sh label: 'Send Google Chat message', script: """
curl -s -X POST -H 'Content-Type: application/json; charset=UTF-8' \
  -d @gchat_msg.json "$GCHAT_WEBHOOK" || true
"""
  }
}

