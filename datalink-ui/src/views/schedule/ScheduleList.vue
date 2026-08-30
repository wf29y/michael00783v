<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='调度资源'>
                <a-input v-model='queryParam.resourceName' placeholder='请输入调度资源' />
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
        rowKey='port'
        :columns='columns'
        :data-source='dataSource'
        :pagination='false'
        :loading='loading'
      >

        <span slot='unit' slot-scope='text, record, index'>
          {{ text }} {{ timeUnitMap[record.timeUnit] }}
        </span>

      </a-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>

import { postAction } from '@/api/manage'
import { timeUnitMap } from '@/config/time.config'

export default {
  name: 'ScheduleList',
  components: {},
  data() {
    return {
      columns: [
        {
          title: '调度资源',
          dataIndex: 'resourceName'
        },
        {
          title: '启动延迟',
          dataIndex: 'initialDelay',
          scopedSlots: { customRender: 'unit' }
        },
        {
          title: '调度间隔',
          dataIndex: 'period',
          scopedSlots: { customRender: 'unit' }
        },
        {
          title: '启动时间',
          dataIndex: 'createTime'
        }
      ],
      dataSource: [],
      loading: true,
      queryParam: {},
      timeUnitMap: timeUnitMap
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    loadData() {
      this.loading = true
      postAction('/api/schedule/list', this.queryParam).then(res => {
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
