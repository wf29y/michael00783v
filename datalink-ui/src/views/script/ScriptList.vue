<template>
  <page-header-wrapper>
    <a-card :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='48'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='脚本ID'>
                <a-input v-model='queryParam.scriptId' placeholder='请输入脚本ID' />
              </a-form-item>
            </a-col>
            <a-col :md='7' :sm='24'>
              <a-form-item label='脚本名称'>
                <a-input v-model='queryParam.scriptName' placeholder='请输入脚本名称' />
              </a-form-item>
            </a-col>
            <a-col :md='7' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <div class='table-operator'>
        <a-button type='primary' @click='handleAdd()' icon='plus'>新建脚本</a-button>
      </div>
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
        <span slot='action' slot-scope='text, record'>
          <template>
            <a @click='handleEdit(record)'>编辑</a>
            <a-divider type='vertical' />
            <a-popconfirm v-if='dataSource.length' title='删除此脚本?' @confirm='() => handleDelete(record)'>
              <a href='javascript:;'>删除</a>
            </a-popconfirm>
          </template>
        </span>
      </a-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>

import { postAction } from '@/api/manage'

export default {
  name: 'ScriptList',
  components: {},
  data() {
    return {
      columns: [
        {
          title: '#',
          scopedSlots: { customRender: 'serial' }
        },
        {
          title: '脚本Id',
          dataIndex: 'scriptId'
        },
        {
          title: '脚本名称',
          dataIndex: 'scriptName'
        },
        {
          title: '说明',
          dataIndex: 'description'
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
      postAction('/api/script/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    handleDelete(record) {
      postAction('/api/script/remove', { scriptId: record.scriptId }).then(res => {
        if (res.code === 200) {
          this.$message.info(res.message)
          this.loadData()
        } else {
          this.$message.info(res.message)
        }
      })
    },
    handleAdd() {
      this.$router.push({ name: 'scriptInfo', params: { scriptId: undefined } })
    },
    handleEdit(record) {
      this.$router.push({ name: 'scriptInfo', params: { scriptId: record.scriptId } })
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>
