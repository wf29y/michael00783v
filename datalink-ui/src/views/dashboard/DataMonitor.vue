<template>
  <page-header-wrapper>
    <a-card :bordered="false">
      <div class="table-page-search-wrapper">
        <a-form layout="inline">
          <a-row :gutter="48">
            <a-col :md="8" :sm="24">
              <a-form-item label="设备">
                <a-select
                  showSearch
                  allowClear
                  optionFilterProp="children"
                  :filterOption="filterOption"
                  placeholder="请选择所属设备"
                  @change="deviceChange"
                  v-model="queryParam.dto.deviceId"
                >
                  <a-select-option v-for="item in devices" :key="item.deviceId" :value="item.deviceId">{{
                    item.deviceName
                  }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <a-col :md="8" :sm="24">
              <a-form-item label="通道">
                <a-select
                  showSearch
                  allowClear
                  optionFilterProp="children"
                  :filterOption="filterOption"
                  placeholder="请选择所属设备"
                  v-model="queryParam.dto.channelId"
                >
                  <a-select-option v-for="item in channels" :key="item.channelId" :value="item.channelId">{{
                    item.channelName
                  }}</a-select-option>
                </a-select>
              </a-form-item>
            </a-col>
            <template v-if="advanced">
              <a-col :md="8" :sm="24">
                <a-form-item label="数据名称">
                  <a-input v-model="queryParam.topic" placeholder="" />
                </a-form-item>
              </a-col>
            </template>
            <a-col :md="(!advanced && 8) || 24" :sm="24">
              <span
                class="table-page-search-submitButtons"
                :style="(advanced && { float: 'right', overflow: 'hidden' }) || {}"
              >
                <a-button type="primary" @click="$refs.table.refresh(true)">查询</a-button>
                <a-button style="margin-left: 8px" @click="() => (this.queryParam = {})">重置</a-button>
                <a @click="toggleAdvanced" style="margin-left: 8px">
                  {{ advanced ? '收起' : '展开' }}
                  <a-icon :type="advanced ? 'up' : 'down'" />
                </a>
              </span>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <div class="table-operator"></div>
      <s-table
        ref="table"
        size="default"
        rowKey="key"
        :columns="columns"
        :data="loadData"
        :alert="true"
        :rowSelection="rowSelection"
        showPagination="auto"
      >
        <span slot="serial" slot-scope="text, record, index">
          {{ index + 1 }}
        </span>
        <span slot="timeStamp" slot-scope="text">
          {{ getBjTime(text) }}
        </span>
      </s-table>
    </a-card>
  </page-header-wrapper>
</template>

<script>
import moment from 'moment'
import { STable, Ellipsis } from '@/components'
import { postAction } from '@/api/manage'

const columns = [
  {
    title: '#',
    scopedSlots: { customRender: 'serial' }
  },
  {
    title: '数据名称',
    dataIndex: 'dataName'
  },
  {
    title: '地址',
    dataIndex: 'addr'
  },
  {
    title: '属性',
    dataIndex: 'attribute'
  },
  {
    title: '值',
    dataIndex: 'value'
  },
  {
    title: '时间戳',
    dataIndex: 'timeStamp',
    scopedSlots: { customRender: 'timeStamp' }
  }
]

export default {
  name: 'Subscribes',
  components: {
    STable,
    Ellipsis
  },
  data() {
    this.columns = columns
    return {
      // 高级搜索 展开/关闭
      advanced: false,
      // 查询参数
      queryParam: {
        dto: {
          dataName: '',
          deviceId: '',
          channelId: ''
        }
      },
      devices: [],
      channels: [],
      dataSource: [],
      // 加载数据方法 必须为 Promise 对象
      loadData: parameter => {
        parameter.dto = {}
        const requestParameters = Object.assign({}, parameter.dto, this.queryParam)
        console.log('loadData request parameters:', requestParameters)
        return postAction(this.url.listTagFromDeviceChannel, requestParameters).then(res => {
          return res.data
        })
      },
      selectedRowKeys: [],
      selectedRows: [],
      url: {
        listPageNorthwardDevice: '/v1/southward/device/listPageNorthwardDevice',
        listChannelFromDevice: '/v1/southward/device/listChannelFromDevice',
        listTagFromDeviceChannel: '/v1/southward/device/listTagFromDeviceChannel'
      }
    }
  },
  filters: {},
  created() {
    this.loadDeviceData()
  },
  computed: {
    rowSelection() {
      return {
        selectedRowKeys: this.selectedRowKeys,
        onChange: this.onSelectChange
      }
    }
  },
  methods: {
    onSelectChange(selectedRowKeys, selectedRows) {
      this.selectedRowKeys = selectedRowKeys
      this.selectedRows = selectedRows
    },
    toggleAdvanced() {
      this.advanced = !this.advanced
    },
    resetSearchForm() {
      this.queryParam = {
        date: moment(new Date())
      }
    },
    getBjTime(time) {
      return moment(time)
        .format('YYYY/MM/DD HH:mm:ss')
        .toString()
    },
    loadDeviceData() {
      postAction(this.url.listPageNorthwardDevice, { page: 1, limit: 1000 }).then(res => {
        this.devices = res.data.data
      })
    },
    loadChannelDataByDevice(deviceId) {
      postAction(this.url.listChannelFromDevice, {
        page: 1,
        limit: 1000,
        dto: { deviceId: deviceId, channelName: '' }
      }).then(res => {
        this.channels = res.data.data
      })
    },
    deviceChange(deviceId) {
      this.loadChannelDataByDevice(deviceId)
    },
    filterOption(input, option) {
      if (!option.componentOptions.children[0].text) {
        return false
      }

      return option.componentOptions.children[0].text.toLowerCase().indexOf(input.toLowerCase()) >= 0
    }
  }
}
</script>
