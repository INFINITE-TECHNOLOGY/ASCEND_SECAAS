package conf.plugins.authentication


import groovy.time.TimeCategory
import io.infinite.ascend.common.JwtManager
import io.infinite.ascend.granting.model.Authentication
import io.infinite.ascend.granting.model.Authorization
import io.infinite.ascend.granting.model.enums.AuthenticationStatus
import io.infinite.blackbox.BlackBox
import org.apache.commons.lang3.time.FastDateFormat
import org.slf4j.LoggerFactory

def log = LoggerFactory.getLogger(this.getClass())

@BlackBox
Map<String, String> applyPlugin() {
    Map<String, String> authorizedCredentials = new HashMap<>()
    def log = LoggerFactory.getLogger(this.getClass())
    Authentication authentication = binding.getVariable("authentication") as Authentication
    Authorization authorization = binding.getVariable("authorization") as Authorization
    try {
        String appName = authentication.authenticationData.publicCredentials.get("appName")
        String selfIssuedJwt = authentication.authenticationData.privateCredentials.get("selfIssuedJwt")
        if (appName == null || selfIssuedJwt == null) {
            log.warn("Missing appName or selfIssuedJwt")
            authentication.status = AuthenticationStatus.FAILED
            return null
        }
        JwtManager jwtManager = new JwtManager()
        jwtManager.jwtAccessKeyPublic = jwtManager.loadPublicKeyFromEnv("PUBLIC_KEY_" + appName)
        Authorization selfIssuedAuthorization = jwtManager.accessJwt2authorization(selfIssuedJwt)
        log.debug(FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS").format(new Date()))
        log.debug(FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS").format(selfIssuedAuthorization.expiryDate))
        if (selfIssuedAuthorization.expiryDate.before(new Date())) {
            log.warn("Expired selfIssuedJwt")
            authentication.status = AuthenticationStatus.FAILED
            return null
        }
        authentication.status = AuthenticationStatus.SUCCESSFUL
        return ["clientId": authentication.authenticationData.publicCredentials.get("clientId")]
    } catch (Exception e) {
        log.error(e.getMessage(), e)
        log.info(authentication.toString())
        log.info(authorization.toString())
        return null
    }
}

return applyPlugin()