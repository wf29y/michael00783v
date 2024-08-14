const TYPE_ALL = 'all'
const TYPE_DEST = 'dest'
const TYPE_RESOURCE = 'resource'

const resourceTypeMap = {
  MQTT: 'MQTT Broker',
  MYSQL: 'Mysql',
  POSTGRESQL: 'Postgresql'
}


const resourceTypeList = [
  {
    name: 'MQTT Broker',
    code: 'MQTT',
    type: TYPE_ALL
  },
  {
    name: 'Mysql',
    code: 'MYSQL',
    type: TYPE_DEST
  },
  {
    name: 'Postgresql',
    code: 'POSTGRESQL',
    type: TYPE_DEST
  }
]

function getResourceTypeList(type) {
  if (!type) return resourceTypeList
  let result = []
  resourceTypeList.forEach(resource => {
    if (resource.type === 'all' || resource.type === type) {
      result.push(resource)
    }
  })
  return result
}


export {
  resourceTypeMap, getResourceTypeList
}

