<template>
  <a-row :gutter='20'>
    <a-form-model layout='vertical' :model='properties'>
      <a-col :span='24' v-if='type==="source"'>
        <a-form-model-item label='时间单位'>
          <a-select v-model='properties.timeUnit' placeholder='请选择时间单位' style='width: 100%'>
            <a-select-option v-for='(item,index) in timeUnitList' :key='index' :value='item.value'>{{ item.name }}
            </a-select-option>
          </a-select>
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='启动延迟'>
          <a-input-number v-model='properties.initialDelay' placeholder='请输入启动延迟' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='12' v-if='type==="source"'>
        <a-form-model-item label='查询频率'>
          <a-input-number v-model='properties.period' placeholder='请输入查询频率' style='width: 100%' />
        </a-form-model-item>
      </a-col>
      <a-col :span='24' class='sql'>
        <a-form-model-item label='命令模板' style='margin-bottom: 0'>
          <monaco-editor ref='MonacoEditor' language='command'></monaco-editor>
        </a-form-model-item>
      </a-col>
    </a-form-model>
  </a-row>
</template>

<script>
import MonacoEditor from '@/components/Editor/MonacoEditor'
import { timeUnitList } from '@/config/time.config'

export default {
  components: { MonacoEditor },
  data() {
    return {
      properties: {},
      timeUnitList: timeUnitList
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
      this.properties = properties
      this.$nextTick(()=> {
        this.$refs.MonacoEditor.set(this.properties.command)
      })
    },
    get() {
      this.properties.command = this.$refs.MonacoEditor.get()
      return this.properties
    }
  }
}
</script>

<style>


</style>
