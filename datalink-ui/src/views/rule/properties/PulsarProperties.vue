<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>

      <a-col :span='24'>
        <a-form-model-item label='Topic' prop='topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>

      <a-col :span='12' v-if="type==='source'">
        <a-form-model-item label='多订阅' prop='multiTopic'>
          <a-select v-model='properties.multiTopic' placeholder='请选择是否多订阅'>
            <tag-select-option :value='true'>是</tag-select-option>
            <tag-select-option :value='false'>否</tag-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>

      <a-col :span='12' v-if="type==='source'">
        <a-form-model-item label='订阅名称' prop='subscriptionName'>
          <a-input v-model='properties.subscriptionName' placeholder='请输入订阅名称' />
        </a-form-model-item>
      </a-col>

      <a-col :span='12' v-if="type==='source'">
        <a-form-model-item label='订阅类型' prop='subscriptionType'>
          <a-select v-model='properties.subscriptionType' placeholder='请选择订阅类型'>
            <tag-select-option value='Exclusive'>Exclusive</tag-select-option>
            <tag-select-option value='Shared'>Shared</tag-select-option>
            <tag-select-option value='Failover'>Failover</tag-select-option>
            <tag-select-option value='Key_Shared'>Key_Shared</tag-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>

      <a-col :span='12' v-if="type==='source'">
        <a-form-model-item label='订阅模式' prop='subscriptionMode'>
          <a-select v-model='properties.subscriptionMode' placeholder='请选择是订阅模式'>
            <tag-select-option value='Durable'>Durable</tag-select-option>
            <tag-select-option value='NonDurable'>NonDurable</tag-select-option>
          </a-select>
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
        multiTopic: false,
        subscriptionType: 'Exclusive',
        subscriptionMode: 'Durable'
      },
      rules: {
        topic: [{ required: true, message: '请输入Topic', trigger: 'blur' }],
        multiTopic: [{ required: true, message: '请选择是否多订阅', trigger: 'change' }],
        subscriptionName: [{ required: true, message: '请输入订阅名称', trigger: 'blur' }],
        subscriptionType: [{ required: true, message: '请选择订阅类型', trigger: 'change' }],
        subscriptionMode: [{ required: true, message: '请选择订阅模式', trigger: 'change' }]
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
      if (this.properties.model === 'queue') {
        delete this.properties.topic
      } else {
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
