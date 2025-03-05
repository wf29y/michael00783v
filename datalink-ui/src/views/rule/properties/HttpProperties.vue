<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24'>
        <a-form-model-item label='路径'>
          <a-input v-model='properties.path' placeholder='请输入路径' :addon-before='properties.url' />
        </a-form-model-item>
      </a-col>
      <a-col :span='type==="source"?12:24'>
        <a-form-model-item label='请求方式'>
          <a-select v-model='properties.method' placeholder='请选择请求方式' style='width: 100%'>
            <a-select-option v-for='(item,index) in methodList' :key='index' :value='item'>{{ item }}</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='时间单位'>
          <a-select v-model='properties.timeUnit' placeholder='请选择时间单位' style='width: 100%'>
            <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='启动延迟'>
          <a-input-number v-model='properties.initialDelay' placeholder='请输入启动延迟' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='请求频率'>
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

import HttpHeadersModel from './HttpHeadersModel'
import MonacoEditor from '@/components/Editor/MonacoEditor'



export default {
  components: { MonacoEditor,HttpHeadersModel },
  data() {
    return {
      properties: {
        method: 'GET',
        headers: { 'Content-Type': 'application/json' },
        body:''
      },
      methodList: ['GET', 'POST', 'PUT', 'DELETE', 'HEAD', 'PATCH', 'OPTIONS', 'TRACE'],
      timeUnitList: [
        { name: '秒', value: 'SECONDS' },
        { name: '分', value: 'MINUTES' },
        { name: '时', value: 'HOURS' },
        { name: '天', value: 'DAYS' }
      ]
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
      this.$refs.HttpHeadersModel.set(this.properties.headers)
      this.$refs.MonacoEditor.set(this.properties.body)
    },
    get() {
      this.properties.headers = this.$refs.HttpHeadersModel.get()
      this.properties.body = this.$refs.MonacoEditor.get()
      return this.properties
    }
  }
}
</script>

<style>


</style>
