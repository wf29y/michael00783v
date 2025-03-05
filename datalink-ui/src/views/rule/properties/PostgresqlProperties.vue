<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24' class='sql'>
        <a-form-model-item label='SQL模板' v-if="type==='dest'" style='margin-bottom: 0'>
          <monaco-editor ref='MonacoEditor' language='sql'></monaco-editor>
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
      properties: {}
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
      this.$refs.MonacoEditor.set(this.properties.sql)
      this.properties = properties
    },
    get() {
      this.properties.sql = this.$refs.MonacoEditor.get()
      return this.properties
    }
  }
}
</script>

<style>


</style>
