<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24'>
        <a-form-model-item label='Topic'>
          <a-input v-model='properties.topic' placeholder='请输入Topic' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24'>
        <a-form-model-item label='消费组' v-if="type==='source'">
          <a-input v-model='properties.group' placeholder='请输入消费组' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='payload'>
        <a-form-model-item label='消息模板' v-if="type==='dest'" style='margin-bottom: 0'>
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
      this.$refs.MonacoEditor.set(this.properties.payload)
    },
    get() {
      this.properties.payload = this.$refs.MonacoEditor.get()
      return this.properties
    }
  }
}
</script>

<style>


</style>
