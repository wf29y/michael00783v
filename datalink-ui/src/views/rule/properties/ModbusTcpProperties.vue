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
          <a-input v-model='properties.cronExpression' placeholder='请输入Cron表达式' />
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import { timeUnitList } from '@/config/time.config'


export default {
  components: {
  },
  data() {
    return {
      properties: {
        points: [],
        initialDelayUnit: 'SECONDS'
      },
      timeUnitList: timeUnitList,
      rules: {
        initialDelay: [{ required: true, message: '请输入启动延迟', trigger: 'blur' }],
        initialDelayUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
        cronExpression: [{ required: true, message: '请输入Cron表达式', trigger: 'blur' }]
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
    },
  }
}
</script>

<style>


</style>
