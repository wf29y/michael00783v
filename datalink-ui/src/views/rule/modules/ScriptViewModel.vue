<template>
  <a-modal
    :title='scriptLanguageName + "脚本"'
    :width='900'
    :visible='visible'
    @cancel='onClose'
    :bodyStyle='{padding:0}'
    :destroyOnClose='true'
    :footer='null'
  >

    <div class='scriptView'>

      <monaco-editor ref='MonacoEditor' :read-only='true' height='500px' :border='false'
                     :minimap='true' :auto-init='false'></monaco-editor>

    </div>


  </a-modal>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'
import { scriptLanguageMap } from '@/config/language.config'

export default {
  name: 'ScriptViewModel',
  components: { MonacoEditor },
  data() {
    return {
      visible: false,
      properties: {},
      scriptLanguageMap: scriptLanguageMap
    }
  },
  computed: {
    scriptLanguageName() {
      return this.properties && this.properties.language ? this.scriptLanguageMap[this.properties.language].name : ''
    }
  },
  methods: {
    show(properties) {
      this.visible = true
      this.properties = JSON.parse(JSON.stringify(properties))
      this.$nextTick(() => {
        this.$refs.MonacoEditor.init(properties.script, this.scriptLanguageMap[properties.language].editor)
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
