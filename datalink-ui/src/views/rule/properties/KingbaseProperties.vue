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
        <a-form-model-item label='执行间隔' prop='interval'>
          <a-input v-model='properties.interval' placeholder='请输入执行间隔' style='width: 100%'>
            <a-select slot='addonAfter' v-model='properties.intervalUnit' placeholder='单位' style='width: 80px'>
              <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
              </a-select-option>
            </a-select>
          </a-input>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='sql'>
        <a-form-model-item label='SQL模板'prop='sql'>
          <monaco-editor ref='MonacoEditor' language='sql'></monaco-editor>
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
      properties: {
        initialDelayUnit: 'SECONDS',
        intervalUnit: 'SECONDS'
      },
      timeUnitList: timeUnitList,
      rules: {
        sql: [{ required: true, validator: this.checkEditor, message: '请输入SQL模板', trigger: 'blur' }],
        initialDelay: [{ required: true, message: '请输入启动延迟', trigger: 'blur' }],
        initialDelayUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }],
        interval: [{ required: true, message: '请输入执行间隔', trigger: 'blur' }],
        intervalUnit: [{ required: true, message: '请选择时间单位', trigger: 'change' }]
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
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(this.properties.sql)
      })
    },
    get(callback) {
      this.properties.sql = this.$refs.MonacoEditor.get()
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
