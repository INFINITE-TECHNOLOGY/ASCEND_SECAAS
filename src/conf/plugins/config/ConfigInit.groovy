package conf.plugins.config

import io.infinite.ascend.config.entities.*
import io.infinite.ascend.config.repositories.*
import io.infinite.blackbox.BlackBox
import org.slf4j.LoggerFactory

def log = LoggerFactory.getLogger(this.getClass())

@BlackBox
void applyPlugin() {
    orbit()
}

@BlackBox
void orbit() {
    def log = LoggerFactory.getLogger(this.getClass())
    GrantRepository grantRepository = binding.getVariable("grantRepository") as GrantRepository
    Set<Grant> grants = new HashSet<Grant>()
    log.info("Initializing config")
    grants.add(new Grant(httpMethod: "POST", urlRegex: "https:\\/\\/orbit-secured\\.herokuapp\\.com\\/orbit\\/%clientId%\\/managedEmail"))
    grants.add(new Grant(httpMethod: "POST", urlRegex: "https:\\/\\/orbit-secured\\.herokuapp\\.com\\/orbit\\/%clientId%\\/managedSms"))
    grantRepository.saveAll(grants)
    grantRepository.flush()
    ScopeRepository scopeRepository = binding.getVariable("scopeRepository") as ScopeRepository
    Set<Scope> scopes = new HashSet<Scope>()
    scopes.add(new Scope(name: "Orbit", grants: grants))
    scopeRepository.saveAll(scopes)
    scopeRepository.flush()
    AuthenticationTypeRepository authenticationTypeRepository = binding.getVariable("authenticationTypeRepository") as AuthenticationTypeRepository
    Set<AuthenticationType> authenticationTypes = new HashSet<AuthenticationType>()
    authenticationTypes.add(new AuthenticationType(name: "JWT"))
    authenticationTypeRepository.saveAll(authenticationTypes)
    authenticationTypeRepository.flush()
    IdentityTypeRepository identityTypeRepository = binding.getVariable("identityTypeRepository") as IdentityTypeRepository
    Set<IdentityType> identityTypes = new HashSet<IdentityType>()
    identityTypes.add(new IdentityType(name: "Trusted Application", authenticationTypes: authenticationTypes))
    identityTypeRepository.saveAll(identityTypes)
    identityTypeRepository.flush()
    AuthorizationTypeRepository authorizationTypeRepository = binding.getVariable("authorizationTypeRepository") as AuthorizationTypeRepository
    Set<AuthorizationType> authorizationTypes = new HashSet<AuthorizationType>()
    authorizationTypes.add(new AuthorizationType(name: "App2app", identityTypes: identityTypes, scopes: scopes
            , durationSeconds: 30
            , maxUsageCount: 1
            , isRefreshAllowed: false
    ))
    authorizationTypeRepository.saveAll(authorizationTypes)
    authorizationTypeRepository.flush()
}

applyPlugin()
return