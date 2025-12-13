<template>
  <a-modal
    title='转换插件'
    :width='900'
    :visible='visible'
    @cancel='handleCancel'
    :destroyOnClose='true'
    :maskClosable='false'
    :footer='null'
    :bodyStyle='{padding:0}'
  >
    <div style='min-height: 400px'>

      <a-table :columns='columns' :data-source='pluginList' size='small' :pagination='false'>
          <span slot='action' slot-scope='text,record'>
            <a @click='select(record)'>选择</a>
          </span>
      </a-table>

    </div>
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
      url: {
        plugin: '/api/plugin/list'
      },
      columns: [
        {
          title: '插件名称',
          dataIndex: 'pluginName'
        },
        {
          title: '说明',
          dataIndex: 'description',
          scopedSlots: { customRender: 'description' }
        },
        {
          title: '操作',
          dataIndex: 'action',
          align: 'center',
          scopedSlots: { customRender: 'action' }
        }
      ],
      pluginList: [],
      transformIndex: -1,
      transform: {}
    }
  },
  mounted() {
    this.init();
  },
  methods: {
    init() {
      this.transformIndex = -1
      this.transform = {
        transformMode: 'PLUGIN',
        properties: {
          plugin: {}
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

      postAction(this.url.plugin, {}).then(res => {
        if (res.code === 200) {
          this.pluginList = res.data
        }
      })

    },
    select(record) {
      this.transform.properties.plugin = JSON.parse(JSON.stringify(record))
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
    }
  }
}
</script>

<style>

</style>
