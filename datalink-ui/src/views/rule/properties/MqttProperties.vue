<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='Topic' prop='topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>
      <a-col :span='8' v-if="type==='dest'">
        <a-form-model-item label='消息保留' prop='retained'>
          <a-select v-model='properties.retained' placeholder='请选择是否消息保留'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span="type==='dest'?8:24">
        <a-form-model-item label='Qos' prop='qos'>
          <a-input-number v-model='properties.qos' placeholder='请输入Qos' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span="type==='dest'?8:24">
        <a-form-model-item label='连接数' prop='poolSize'>
          <a-input-number v-model='properties.poolSize' placeholder='请输入连接数' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload' v-if="type==='dest'">
        <a-form-model-item label='消息模板'  style='margin-bottom: 0'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
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
        poolSize: 10,
        retained: false
      },
      rules: {
        topic: [{ required: true, message: '请输入Topic', trigger: 'blur' }],
        retained: [{ required: true, message: '请选择是否消息保留', trigger: 'change' }],
        qos: [{ required: true, message: '请输入Qos', trigger: 'blur' }]
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
    get(callback) {
      if (this.type === 'dest') {
        this.properties.payload = this.$refs.MonacoEditor.get()
      }
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
