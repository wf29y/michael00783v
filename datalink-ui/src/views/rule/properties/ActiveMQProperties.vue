<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>

      <a-col :span='24'>
        <a-form-model-item label='模式' prop='model'>
          <a-select v-model='properties.model' placeholder='请选择模式'>
            <tag-select-option value='queue'>Queue</tag-select-option>
            <tag-select-option value='topic'>Topic</tag-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>

      <a-col :span='24' v-if='properties.model==="queue"'>
        <a-form-model-item label='Queue' prop='queue'>
          <a-input v-model='properties.queue' placeholder='请输入Queue' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if='properties.model==="topic"'>
        <a-form-model-item label='Topic' prop='topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>

      <a-col :span='24' class='payload' v-if="type==='dest'">
        <a-form-model-item label='消息模板' style='margin-bottom: 0'>
          <monaco-editor ref='MonacoEditor' language='freemarker2'></monaco-editor>
        </a-form-model-item>
      </a-col>

    </a-form-model>
  </a-row>
</template>

<script>

import MonacoEditor from '@/components/Editor/MonacoEditor'
import TagSelectOption from '@/components/TagSelect/TagSelectOption'


export default {
  components: { TagSelectOption, MonacoEditor },
  data() {
    return {
      properties: {
        model: 'queue'
      },
      rules: {
        topic: [{ required: true, message: '请输入Topic', trigger: 'blur' }],
        queue: [{ required: true, message: '请输入Queue', trigger: 'change' }],
        model: [{ required: true, message: '请选择消费模式', trigger: 'change' }]
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
      if(this.properties.model==='queue'){
        delete this.properties.topic
      }else {
        delete this.properties.queue
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
