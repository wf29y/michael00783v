<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24' v-if='type==="source"'>
        <a-form-model-item label='时间单位' prop='timeUnit'>
          <a-select v-model='properties.timeUnit' placeholder='请选择时间单位' style='width: 100%'>
            <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='启动延迟' prop='initialDelay'>
          <a-input-number v-model='properties.initialDelay' placeholder='请输入启动延迟' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='查询频率' prop='period'>
          <a-input-number v-model='properties.period' placeholder='请输入查询频率' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='sql'>
        <a-form-model-item label='命令模板' style='margin-bottom: 0' prop='command'>
          <monaco-editor ref='MonacoEditor' language='redis'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'
import { timeUnitList } from '@/config/time.config'

export default {
  components: { MonacoEditor },
  data() {
    return {
      properties: {},
      timeUnitList: timeUnitList,
      rules: {
        command: [{ required: true, validator: this.checkEditor, message: '请输入命令模板', trigger: 'blur' }],
        timeUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
        initialDelay: [{ required: true, message: '请输入启动延迟', trigger: 'blur' }],
        period: [{ required: true, message: '请输入请求频率', trigger: 'blur' }]
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
      this.properties = properties
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.command)
      })
    },
    get(callback) {
      this.properties.command = this.$refs.MonacoEditor.get()
      let that = this
      this.$refs.propForm.validate(valid => {
        if (valid) {
          return callback(true, that.properties)
        } else {
          return callback(false)
        }
      })
    },
    checkEditor(rule, value, callback) {
      let content = this.$refs.MonacoEditor.get()
      if (!content) {
        return callback(new Error())
      } else {
        return callback()
      }
    }
  }
}
</script>

<style>


</style>
