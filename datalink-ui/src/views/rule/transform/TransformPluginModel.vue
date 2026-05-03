<template>
  <a-modal
    title='转换插件'
    :width='900'
    :visible='visible'
    @cancel='handleCancel'
    :destroyOnClose='true'
    :maskClosable='false'
    :bodyStyle='{padding:0}'
  >
    <div style='min-height: 400px'>
      <a-table :columns='columns'
               :data-source='pluginList'
               size='small'
               :pagination='false'
               rowKey='pluginId'
               :rowSelection="{ type:'radio',selectedRowKeys:selectedRowKeys,onChange:onSelectChange }">

        <span slot='description' slot-scope='text, record, index'>
          {{ text ? text : '-' }}
        </span>

      </a-table>
    </div>

    <template slot='footer'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='6' :sm='24'>
              <a-form-item label='处理器数量' style='margin-bottom: 0'>
                <a-input-number v-model='transform.workerNum' placeholder='处理器数量' style='width: 100%' :min='1' />
              </a-form-item>
            </a-col>
            <a-col :md='18' :sm='24'>
              <a-button key='submit' type='primary' @click='handleOk'>确定</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </template>

  </a-modal>
</template>

<script>
import { postAction } from '@/api/manage'

export default {
  name: 'TransformPluginModel',
  components: {},
  data() {
    return {
      visible: false,
      selectedRowKeys: [],
      url: {
        plugin: '/api/plugin/list'
      },
      columns: [
        {
          title: '名称',
          dataIndex: 'pluginName'
        },
        {
          title: '包路径',
          dataIndex: 'packagePath'
        },
        {
          title: '说明',
          dataIndex: 'description',
          scopedSlots: { customRender: 'description' }
        }
      ],
      pluginList: [],
      transformIndex: -1,
      transform: {}
    }
  },
  mounted() {
    this.init()
  },
  methods: {
    init() {
      this.transformIndex = -1
      this.transform = {
        transformMode: 'PLUGIN',
        workerNum: 3,
        properties: {
          plugin: {}
        }
      }
    },
    add() {
      this.init()
      this.edit(this.transform, -1)
    },
    edit(transform, index) {
      this.visible = true
      this.transform = JSON.parse(JSON.stringify(transform))
      this.transformIndex = index

      if (this.transform.properties.plugin.pluginId) {
        this.selectedRowKeys = [this.transform.properties.plugin.pluginId]
      } else {
        this.selectedRowKeys = []
      }

      postAction(this.url.plugin, {}).then(res => {
        if (res.code === 200) {
          this.pluginList = res.data
        }
      })

    },
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.transform.properties.plugin = JSON.parse(JSON.stringify(selectedRows[0]))
    },
    handleOk() {
      if(this.selectedRowKeys.length === 0){
        this.$message.error('请选择一个插件')
        return
      }
      if (this.transformIndex >= 0) {
        this.$emit('update', this.transform, this.transformIndex)
      } else {
        this.$emit('add', this.transform)
      }
      this.visible = false
    },
    handleCancel() {
      this.init()
      this.visible = false
    }
  }
}
</script>

<style>

</style>
