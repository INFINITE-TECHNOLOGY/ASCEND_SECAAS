package io.infinite.ascend

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j
import io.infinite.ascend.granting.configuration.entities.*
import io.infinite.ascend.granting.configuration.repositories.*
import io.infinite.blackbox.BlackBox
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
@Slf4j
@BlackBox
@CompileStatic
class ConfigInitService {

    @Autowired
    PrototypeGrantRepository grantRepository

    @Autowired
    PrototypeScopeRepository scopeRepository

    @Autowired
    PrototypeAuthenticationRepository authenticationTypeRepository

    @Autowired
    PrototypeIdentityRepository identityTypeRepository

    @Autowired
    PrototypeAuthorizationRepository authorizationTypeRepository

    @BlackBox
    void initConfig() {
        Set<PrototypeGrant> grants = new HashSet<PrototypeGrant>()
        log.info("Initializing config")
        grants.add(new PrototypeGrant(httpMethod: "POST", urlRegex: "https:\\/\\/orbit-secured\\.herokuapp\\.com\\/orbit\\/%appName%\\/managedEmail"))
        grants.add(new PrototypeGrant(httpMethod: "POST", urlRegex: "https:\\/\\/orbit-secured\\.herokuapp\\.com\\/orbit\\/%appName%\\/managedSms"))
        grants.add(new PrototypeGrant(httpMethod: "GET", urlRegex: "https:\\/\\/orbit-secured\\.herokuapp\\.com\\/orbit\\/templates\\/search\\/findByAppName\\?appName=%appName%"))
        grants.add(new PrototypeGrant(httpMethod: "POST", urlRegex: "https:\\/\\/orbit-secured\\.herokuapp\\.com\\/orbit\\/templates"))
        grantRepository.saveAll(grants)
        grantRepository.flush()
        Set<PrototypeScope> scopes = new HashSet<PrototypeScope>()
        scopes.add(new PrototypeScope(name: "Orbit", grants: grants))
        scopeRepository.saveAll(scopes)
        scopeRepository.flush()
        Set<PrototypeAuthentication> authenticationTypes = new HashSet<PrototypeAuthentication>()
        authenticationTypes.add(new PrototypeAuthentication(name: "JWT"))
        authenticationTypeRepository.saveAll(authenticationTypes)
        authenticationTypeRepository.flush()
        Set<PrototypeIdentity> identityTypes = new HashSet<PrototypeIdentity>()
        identityTypes.add(new PrototypeIdentity(name: "Trusted Application", authentications: authenticationTypes))
        identityTypeRepository.saveAll(identityTypes)
        identityTypeRepository.flush()
        Set<PrototypeAuthorization> authorizationTypes = new HashSet<PrototypeAuthorization>()
        authorizationTypes.add(new PrototypeAuthorization(name: "App2app", identities: identityTypes, scopes: scopes
                , durationSeconds: 3000
                , maxUsageCount: 100
        ))
        authorizationTypeRepository.saveAll(authorizationTypes)
        authorizationTypeRepository.flush()
    }
}