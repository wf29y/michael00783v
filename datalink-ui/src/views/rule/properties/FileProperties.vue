<template>
  <a-row :gutter='24'>
    <a-form-model layout='vertical' :model='properties' ref='propForm' :rules='rules'>
      <a-col :span='24'>
        <a-form-model-item label='文件路径' prop='file'>
          <a-input v-model='properties.file' placeholder='请输入文件路径' :addon-before='properties.path' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' v-if="type==='source'">
        <a-form-model-item label='监听延迟(ms)' prop='delay'>
          <a-input-number v-model='properties.delay' placeholder='请输入监听延迟' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="type==='dest'">
        <a-form-model-item label='缓存数量' prop='buffer'>
          <a-input-number v-model='properties.buffer' placeholder='请输入缓存数量' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if="type==='dest'">
        <a-form-model-item label='行模式' prop='lineMode'>
          <a-select v-model='properties.lineMode' placeholder='请选择是否行模式'>
            <a-select-option :value='true'>是</a-select-option>
            <a-select-option :value='false'>否</a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='content' v-if="type==='dest'">
        <a-form-model-item label='内容模板'>
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
      properties: {},
      sourceDefaultValue: {
        delay: 1000
      },
      destDefaultValue: {
        buffer: 5,
        lineMode: true
      },
      rules: {
        file: [{ required: true, message: '请输入存储路径', trigger: 'blur' }],
        delay: [{ required: true, message: '请输入监听延迟', trigger: 'blur' }],
        buffer: [{ required: true, message: '请输入缓存数量', trigger: 'blur' }],
        lineMode: [{ required: true, message: '请选择是否行模式', trigger: 'change' }]
      }
    }
  },
  props: {
    type: { // dest or source
      type: String,
      default: undefined
    }
  },
  mounted() {
    this.properties = Object.assign({}, this.type === 'source' ? this.sourceDefaultValue : this.destDefaultValue)
  },
  methods: {
    set(properties) {
      this.properties = Object.assign({}, this.properties, properties)
      if (this.type === 'dest') {
        this.$nextTick(() => {
          this.$refs.MonacoEditor.set(this.properties.content)
        })
      }
    },
    get(callback) {
      if (this.type === 'dest') {
        this.properties.content = this.$refs.MonacoEditor.get()
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
