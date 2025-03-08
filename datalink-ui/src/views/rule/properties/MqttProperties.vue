<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24'>
        <a-form-model-item label='Topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="type==='dest'">
        <a-form-model-item label='消息保留'>
          <a-select v-model='properties.retained' placeholder='请选择是否消息保留'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span="type==='dest'?12:24">
        <a-form-model-item label='Qos'>
          <a-input-number v-model='properties.qos' placeholder='请输入Qos' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload' v-if="type==='dest'">
        <a-form-model-item label='消息模板'  style='margin-bottom: 0'>
          <monaco-editor ref='MonacoEditor'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor'


export default {
  components: { MonacoEditor },
  data() {
    return {
      properties: {
        qos: 0,
        retained: false
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
      if (this.type === 'dest') {
        this.$nextTick(() => {
          this.$refs.MonacoEditor.set(this.properties.payload)
        })
      }
    },
    get() {
      if (this.type === 'dest') {
        this.properties.payload = this.$refs.MonacoEditor.get()
      }
      return this.properties
    }
  }
}
</script>

<style>


</style>
