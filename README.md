# ferryman
基于netty开发的网关项目,支持接收http请求，转发到下游http服务

网关仅依赖ETCD作为配置

当前还没开发控制台

如果需要跑起来，需要手动添加路由规则：

每个协议可以绑定一个规则

比如，在ETCD上手动添加一个路由规则，ID为1：

{"filterConfigs":[{"config":"{\"loggable\":\"false\"}","id":"defaultErrorFilter"},{"config":"{\"loggable\":\"false\",\"loadBalanceStrategy\":\"RANDOM\"}","id":"loadBalancePreFilter"},{"config":"{\"loggable\":\"true\"}","id":"httpRouteFilter"},{"config":"{\"loggable\":\"false\",\"timeout\":\"4000\"}","id":"timeoutPreFilter"}],"id":"1","name":"测试规则1","order":1,"protocol":"http"}


给每一个方法都绑定这个路由规则：

{"enable":true,"envType":"dev","invokerMap":{"/testEntity":{"ruleId":"1","invokerPath":"/testEntity","timeout":5000},"/testParam":{"ruleId":"1","invokerPath":"/testParam","timeout":5000},"/testPost":{"ruleId":"1","invokerPath":"/testPost","timeout":5000},"/testGet":{"ruleId":"1","invokerPath":"/testGet","timeout":5000}},"patternPath":"/test*","protocol":"http","serviceId":"hello","uniqueId":"hello:1.0.0","version":"1.0.0"}

