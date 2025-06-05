<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='20'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='插件名称'>
                <a-input v-model='queryParam.pluginName' placeholder='请输入插件名称' />
              </a-form-item>
            </a-col>
            <a-col :md='14' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='3' :sm='24' style='text-align: right'>
              <a-button type='primary' @click='handleAdd()' icon='plus'>新建插件</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>
    <a-card :body-style='{minHeight:"500px"}' :bordered='false'>
      <a-table
        ref='table'
        :columns='columns'
        :data-source='dataSource'
        :pagination='false'
        :loading='loading'
      >
        <span slot='serial' slot-scope='text, record, index'>
          {{ index + 1 }}
        </span>

        <span slot='description' slot-scope='text, record, index'>
          {{ text ? text : '-' }}
        </span>

        <span slot='action' slot-scope='text, record'>
          <template>
            <a @click='handleEdit(record)'>编辑</a>
            <a-divider type='vertical' />
            <a-popconfirm v-if='dataSource.length' title='删除此插件?' @confirm='() => handleDelete(record)'>
              <a href='javascript:;'>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </a-table>
    </a-card>
    <plugin-model ref='PluginModel' @ok='loadData'></plugin-model>
  </page-header-wrapper>
</template>

<script>

import { postAction } from '@/api/manage'
import PluginModel from './modules/PluginModel'

export default {
  name: 'ScriptList',
  components: { PluginModel },
  data() {
    return {
      columns: [
        {
          title: '插件名称',
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
        },
        {
          title: '最后修改',
          dataIndex: 'updateTime'
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      dataSource: [],
      loading: true,
      queryParam: {}
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      postAction('/api/plugin/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    handleDelete(record) {
      postAction('/api/plugin/remove', { pluginId: record.pluginId }).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.error('删除失败')
        }
      })
    },
    handleAdd() {
      this.$refs.PluginModel.add()
    },
    handleEdit(record) {
      this.$refs.PluginModel.edit(record)
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>
