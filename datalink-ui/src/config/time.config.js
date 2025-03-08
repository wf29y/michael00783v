const timeUnitMap = {
  SECONDS: '秒',
  MINUTES: '分',
  HOURS: '时',
  DAYS: '天'
}

function createUnitList() {
  let result = []
  for (let key in timeUnitMap) {
    result.push({
      name: timeUnitMap[key],
      value: key
    })
  }
  return result
}

const timeUnitList = createUnitList()
export {
  timeUnitMap, timeUnitList
}
