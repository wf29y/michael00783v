<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='启动延迟' prop='initialDelay'>
          <a-input v-model='properties.initialDelay' placeholder='请输入启动延迟' style='width: 100%'>
            <a-select slot='addonAfter' v-model='properties.initialDelayUnit' placeholder='单位' style='width: 80px'>
              <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
              </a-select-option>
            </a-select>
          </a-input>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='Cron表达式' prop='cronExpression'>
          <easy-cron v-model='properties.cronExpression'></easy-cron>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if='type==="source"'>
        <a-form-model-item label='传输方式' prop='transferType'>
          <a-select v-model='properties.transferType' placeholder='请选择传输方式'>
            <a-select-option value='single'>每个点位逐条传输</a-select-option>
            <a-select-option value='pack'>所有点位打包传输</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if='type==="dest"'>
        <a-form-model-item label='点位地址' prop='address'>
          <a-input v-model='properties.address' placeholder='请输入点位地址' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="dest"'>
        <a-form-model-item label='数据类型' prop='dataType'>
          <a-select v-model='properties.dataType' placeholder='请选择数据类型'>
            <a-select-option v-for='(item,index) in dataTypeList' :key='index' :value='item'>{{ item }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="dest"'>
        <a-form-model-item label='数据值' prop='dataValue'>
          <a-input v-model='properties.dataValue' placeholder='请输入数据值' />
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import { timeUnitList } from '@/config/time.config'


export default {
  data() {
    return {
      properties: {
        points: [],
        initialDelayUnit: 'SECONDS',
        cronExpression: '0 0/1 * * * ? *',
        transferType: 'single'
      },
      timeUnitList: timeUnitList,
      dataTypeList: ['Int16', 'UInt16', 'Int32', 'UInt32', 'Int64', 'UInt64', 'Float', 'Double', 'String', 'Boolean'],
      rules: {
        initialDelay: [{ required: true, message: '请输入启动延迟', trigger: 'blur' }],
        initialDelayUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
        cronExpression: [{ required: true, message: '请输入Cron表达式', trigger: 'blur' }],
        transferType: [{ required: true, message: '请选择传输方式', trigger: 'change' }],
        address: [{ required: true, message: '请输入点位地址', trigger: 'blur' }],
        dataType: [{ required: true, message: '请选择数据类型', trigger: 'change' }],
        dataValue: [{ required: true, message: '请输入数据值', trigger: 'blur' }]
      }
    }
  },
  props: {
    type: { // dest or source
      type: String,
      default: undefined
    }
  },
  methods: {
    set(properties) {
      if (this.type === 'dest') {
        delete this.properties.initialDelayUnit
        delete this.properties.cronExpression
        delete this.properties.points
        delete this.properties.transferType
      }
      this.properties = Object.assign({}, this.properties, properties)
    },
    get(callback) {
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    }
  }
}
</script>

<style>


</style>
