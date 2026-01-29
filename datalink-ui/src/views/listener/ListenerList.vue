<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='监听地址'>
                <a-input v-model='queryParam.port' placeholder='请输入监听地址' />
              </a-form-item>
            </a-col>
            <a-col :md='10' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='7' :sm='24' style='text-align: right'>
              <a-button @click='loadData' icon='undo'>刷新</a-button>
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

        <span slot='port' slot-scope='text, record, index'>
          0.0.0.0:{{ text }}
        </span>

        <span slot='desc' slot-scope='text, record, index'>
          {{ text ? text : '-' }}
        </span>

      </a-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>

import { postAction } from '@/api/manage'

export default {
  name: 'ListenerList',
  components: {},
  data() {
    return {
      columns: [
        /*        {
                  title: '#',
                  scopedSlots: { customRender: 'serial' }
                },*/
        {
          title: '监听地址',
          dataIndex: 'port',
          width: '30%',
          scopedSlots: { customRender: 'port' }
        },
        {
          title: '类型',
          dataIndex: 'type',
          width: '30%'
        },
        {
          title: '说明',
          dataIndex: 'desc',
          scopedSlots: { customRender: 'desc' }
        }
        /*   {
             title: '操作',
             dataIndex: 'action',
             width: '150px',
             scopedSlots: { customRender: 'action' }
           }*/
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
      postAction('/api/listener/list', this.queryParam).then(res => {
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
