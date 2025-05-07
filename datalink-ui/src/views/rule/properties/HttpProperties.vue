<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='路径' prop='path'>
          <a-input v-model='properties.path' placeholder='请输入路径' :addon-before='properties.url' />
        </a-form-model-item>
      </a-col>
      <a-col :span='type==="source"?12:24'>
        <a-form-model-item label='请求方式' prop='method'>
          <a-select v-model='properties.method' placeholder='请选择请求方式' style='width: 100%'>
            <a-select-option v-for='(item,index) in methodList' :key='index' :value='item'>{{ item }}</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
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
        <a-form-model-item label='请求频率' prop='period'>
          <a-input-number v-model='properties.period' placeholder='请输入请求频率' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <http-headers-model ref='HttpHeadersModel'></http-headers-model>
      </a-col>
      <a-col :span='24' class='body'>
        <a-form-model-item label='请求体' style='margin-bottom: 0'>
          <monaco-editor ref='MonacoEditor'></monaco-editor>
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
        body: ''
      },
      methodList: ['GET', 'POST', 'PUT', 'DELETE', 'HEAD', 'PATCH', 'OPTIONS', 'TRACE'],
      timeUnitList: timeUnitList,
      rules: {
        path: [{ required: true, message: '请输入路径', trigger: 'blur' }],
        method: [{ required: true, message: '请选择请求方式', trigger: 'blur' }],
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

<style>


</style>
