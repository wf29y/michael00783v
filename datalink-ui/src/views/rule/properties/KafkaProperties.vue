<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='Topic' prop='topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if="type==='source'">
        <a-form-model-item label='消费组' prop='group'>
          <a-input v-model='properties.group' placeholder='请输入消费组' />
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
      properties: {},
      rules: {
        topic: [{ required: true, message: '请输入Topic', trigger: 'blur' }],
        group: [{ required: true, message: '请输入消费组', trigger: 'blur' }],
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
