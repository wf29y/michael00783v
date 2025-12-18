<template>
  <a-modal
    title='转换SQL'
    :width='800'
    :visible='visible'
    @cancel='handleCancel'
    :destroyOnClose='true'
    :maskClosable='false'
    :bodyStyle='{padding:0}'
  >

    <div class='scriptView'>
      <monaco-editor ref='MonacoEditor' height='400px' :border='false' language='sql'></monaco-editor>
    </div>

    <template slot='footer'>
      <a-button key='back' @click='handleCancel'>取消</a-button>
      <a-button key='submit' type='primary' @click='handleOk'>确定</a-button>
    </template>

  </a-modal>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'

export default {
  name: 'TransformSqlModel',
  components: { MonacoEditor },
  data() {
    return {
      visible: false,
      transformIndex: -1,
      transform: {
        transformMode: 'SQL',
        properties: {
          sql: "SELECT * FROM [resourceRuntimeId]"
        }
      }
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.transformIndex = -1
      this.transform = {
        transformMode: 'SQL',
        properties: {
          sql: "SELECT * FROM [resourceRuntimeId]"
        }
      }
    },
    add() {
      this.init();
      this.edit(this.transform, -1)
    },
    edit(transform, index) {
      this.visible = true
      this.transform = JSON.parse(JSON.stringify(transform))
      this.transformIndex = index
      this.$nextTick(() => {
        this.$refs.MonacoEditor.set(transform.properties.sql)
      })
    },
    handleOk() {
      this.transform.properties.sql = this.$refs.MonacoEditor.get()
      if (this.transformIndex >= 0) {
        this.$emit('update', this.transform, this.transformIndex)
      } else {
        this.$emit('add', this.transform)
      }
      this.visible = false
    },
    handleCancel() {
      this.init();
      this.visible = false
    },
  }
}
</script>

<style>

</style>
