const transformModeMap = {
  WITHOUT: '无转换透传',
  SCRIPT: 'JS脚本转换',
  PLUGIN: '插件转换',
  SQL: 'SQL转换'
}

function createModeList() {
  let result = []
  for (let key in transformModeMap) {
    result.push({
      name: transformModeMap[key],
      value: key
    })
  }
  return result
}

const transformModeList = createModeList()
export {
  transformModeMap, transformModeList
}
