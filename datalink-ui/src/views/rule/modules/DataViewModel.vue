<template>
  <a-modal
    title='最近数据'
    :width='1100'
    :visible='visible'
    @cancel='onClose'
    :bodyStyle='{padding:"10px 15px 15px 15px"}'
    :footer='null'
  >

    <!--    <div class='title'>
          最近数据
          <span @click='refresh' style='cursor: pointer'><a-icon type='redo'></a-icon></span>
        </div>-->
    <a-table :columns='dataColumns' :data-source='dataList' size='middle' :pagination='false'>

      <span slot='data' slot-scope='text,record'>
        <a-popover title='数据'>
                <template slot='content'>
                 {{ text ? text : '—' }}
                </template>
                 {{ text ? text : '—' }}
        </a-popover>
      </span>

      <span slot='error' slot-scope='text,record'>
              <a-popover title='说明'>
                <template slot='content'>
                  {{ record.message ? record.message : '—' }}
                </template>
                  <a-badge v-if='text===true' color='red' text='失败' />
                  <a-badge v-if='text===false' color='green' text='成功' />
              </a-popover>
      </span>
    </a-table>

  </a-modal>
</template>

<script>

export default {
  name: 'DataViewModel',
  data() {
    return {
      visible: false,
      dataList: [],
      dataColumns: [
        {
          title: '时间',
          dataIndex: 'time',
          width: 100
        },
        {
          title: '节点',
          dataIndex: 'memberName',
          width: 130
        },
        {
          title: '状态',
          dataIndex: 'error',
          width: 60,
          scopedSlots: { customRender: 'error' }
        },
        {
          title: '数据',
          dataIndex: 'data',
          scopedSlots: { customRender: 'data' },
          ellipsis: true,
          width: 400
        }
      ]
    }
  },
  methods: {
    show(dataList) {
      this.visible = true
      this.dataList = dataList
    },
    onClose() {
      this.visible = false
      this.dataList = []
    }
  }
}
</script>

<style>

</style>
