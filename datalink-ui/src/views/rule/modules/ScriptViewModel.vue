<template>
  <a-modal
    title='解析脚本'
    :width='900'
    :visible='visible'
    @cancel='onClose'
    :bodyStyle='{padding:0}'
    :footer='null'
  >

    <div class='scriptView'>

      <monaco-editor ref='MonacoEditor' :read-only='true' height='500px' :border='false'
                     :minimap='true'></monaco-editor>

    </div>


  </a-modal>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'

export default {
  name: 'ScriptViewModel',
  components: { MonacoEditor },
  data() {
    return {
      visible: false
    }
  },
  methods: {
    show(script) {
      this.visible = true
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(script)
      })
    },
    onClose() {
      this.visible = false
    }
  }
}
</script>

<style>

</style>
