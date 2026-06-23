<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='6' :sm='24'>
              <a-form-item label='节点'>
                <a-input v-model='queryParam.memberName' placeholder='请输入节点' />
              </a-form-item>
            </a-col>
            <a-col :md='4' :sm='24'>
              <a-form-item label='状态'>
                <a-select v-model='queryParam.memberState' placeholder='请选择状态' >
                  <a-select-option value='UP'>UP</a-select-option>
                  <a-select-option value='DOWN'>DOWN</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md='10' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='4' :sm='24' style='text-align: right'>
              <a-button @click='loadData' icon='undo'>刷新</a-button>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </a-card>
    <a-card :body-style='{minHeight:"500px"}' :bordered='false'>
      <a-table
        ref='table'
        rowKey='port'
        :columns='columns'
        :data-source='dataSource'
        :pagination='false'
        :loading='loading'
      >

        <span slot='memberName' slot-scope='text, record, index'>
            {{ text }}   <a-tag v-if='record.local'>当前节点</a-tag>
        </span>

        <span slot='memberState' slot-scope='text, record, index'>
           <a-tag color="#87d068" v-if='text==="UP"'>UP</a-tag>
           <a-tag color="#f50" v-if='text==="DOWN"'>DOWN</a-tag>
        </span>

      </a-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>

import { postAction } from '@/api/manage'

export default {
  name: 'ClusterList',
  components: {},
  data() {
    return {
      columns: [
        {
          title: '节点',
          dataIndex: 'memberName',
          scopedSlots: { customRender: 'memberName' }
        },
        {
          title: '状态',
          dataIndex: 'memberState',
          scopedSlots: { customRender: 'memberState' }
        },
        {
          title: '更新时间',
          dataIndex: 'updateTime',
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
      postAction('/api/cluster/list', this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>
