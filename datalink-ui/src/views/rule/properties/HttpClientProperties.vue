<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='12'>
        <a-form-model-item label='请求方式' prop='method'>
          <a-select v-model='properties.method' placeholder='请选择请求方式' style='width: 100%'>
            <a-select-option v-for='(item,index) in methodList' :key='index' :value='item'>{{ item }}</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12'>
        <a-form-model-item label='路径' prop='path'>
          <a-input v-model='properties.path' placeholder='请输入路径' :addon-before='properties.url' />
        </a-form-model-item>
      </a-col>
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
      <a-col :span='24'>
        <http-headers-model ref='HttpHeadersModel'></http-headers-model>
      </a-col>
      <a-col :span='24' class='body'>
        <a-form-model-item label='请求体'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import HttpHeadersModel from './model/HttpHeadersModel'
import MonacoEditor from '@/components/Editor/MonacoEditor'
import { timeUnitList } from '@/config/time.config'


export default {
  components: { MonacoEditor, HttpHeadersModel },
  data() {
    return {
      properties: {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        body: '',
        initialDelayUnit: 'SECONDS'
      },
      methodList: ['GET', 'POST', 'PUT', 'DELETE', 'HEAD', 'PATCH', 'OPTIONS', 'TRACE'],
      timeUnitList: timeUnitList,
      rules: {
        path: [{ required: true, message: '请输入路径', trigger: 'blur' }],
        method: [{ required: true, message: '请选择请求方式', trigger: 'blur' }],
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
  mounted() {
    this.$nextTick(() => {
      this.$refs.HttpHeadersModel.set(this.properties.headers)
      this.$refs.MonacoEditor.set(this.properties.body)
    })
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      this.$nextTick(() => {
        this.$refs.HttpHeadersModel.set(this.properties.headers)
        this.$refs.MonacoEditor.set(this.properties.body)
      })
    },
    get(callback) {
      this.properties.headers = this.$refs.HttpHeadersModel.get()
      this.properties.body = this.$refs.MonacoEditor.get()
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

<style scoped>

.ant-form-item-with-help {
  margin-bottom: 6px;
}

</style>
