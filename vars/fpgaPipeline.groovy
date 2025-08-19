def call(Map cfg = [:]) {
  def simEnabled = cfg.get('sim', [:]) != [:]
  def buildEnabled = cfg.get('build', [:]) != [:]
  def deployEnabled = cfg.get('deploy', [:]).get('tftp', [:]).get('enabled', false)
  def notifyCfg = cfg.get('notify', [:]).get('google_chat', [:])
  def notifyEnabled = notifyCfg.get('enabled', false)

  try {
    if (simEnabled) {
      node('docker') {
        stage('VUnit') { vunit(cfg) }
      }
    }
    if (buildEnabled) {
      node('vivado') {
        stage('Vivado Build') { vivadoBuild(cfg) }
      }
    }
    if (deployEnabled) {
      node('boardfarm') {
        stage('TFTP Deploy') { tftpDeploy(cfg) }
      }
    }
    currentBuild.result = 'SUCCESS'
  } catch (Throwable t) {
    currentBuild.result = 'FAILURE'
    throw t
  } finally {
    if (notifyEnabled) {
      gchatNotify(cfg, currentBuild.result ?: 'UNKNOWN')
    }
  }
}

