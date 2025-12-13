<template>
  <page-header-wrapper :breadcrumb='false'>
    <a-card style='margin-bottom: 15px' :body-style='{paddingBottom:0}' :bordered='false'>
      <div class='table-page-search-wrapper'>
        <a-form layout='inline'>
          <a-row :gutter='24'>
            <a-col :md='7' :sm='24'>
              <a-form-item label='规则名称'>
                <a-input v-model='queryParam.ruleName' placeholder='请输入规则名称' />
              </a-form-item>
            </a-col>
            <a-col :md='14' :sm='24'>
              <a-button type='primary' @click='loadData'>查询</a-button>
              <a-button style='margin-left: 8px' @click='reset'>重置</a-button>
            </a-col>
            <a-col :md='3' :sm='24' style='text-align: right'>
              <a-button type='primary' @click='handleAdd()' icon='plus'>新建规则</a-button>
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
        <span slot='serial' slot-scope='text, item, index'>
          {{ index + 1 }}
        </span>

        <span slot='ruleName' slot-scope='text, item, index'>
          <a @click='handleRuntime(item)'>{{ text }}</a>
        </span>

<!--        <span slot='transformModeMap' slot-scope='text, item, index'>
          {{ transformModeMap[item.transformMode] }}
        </span>-->

        <span slot='sourceResourceName' slot-scope='text, item, index'>
        {{ getResourceNameStr(item.sourceResourceList) }}
        </span>

        <span slot='destResourceName' slot-scope='text, item, index'>
          {{ getResourceNameStr(item.destResourceList) }}
        </span>

        <span slot='enable' slot-scope='text, item, index'>
           <a-switch :checked='text' @change='enableChange(item)' />
        </span>

        <span slot='action' slot-scope='text, item'>
              <a @click='handleEdit(item)' :disabled='item.enable'>编辑</a>
             <a-divider type='vertical' />
                <a @click='handleDelete(item)'>删除</a>
        </span>

      </a-table>

    </a-card>
  </page-header-wrapper>
</template>

<script>
import { postAction } from '@/api/manage'
//import { transformModeMap } from '@/config/transform.config'

export default {
  name: 'RuleList',
  components: {},
  data() {
    return {
      loading: true,
      columns: [
        // {
        //   title: '#',
        //   scopedSlots: { customRender: 'serial' }
        // },
        {
          title: '规则名称',
          dataIndex: 'ruleName',
          scopedSlots: { customRender: 'ruleName' }
        },
        // {
        //   title: '转换方式',
        //   dataIndex: 'transformModeMap',
        //   scopedSlots: { customRender: 'transformModeMap' }
        // },
        {
          title: '数据源',
          dataIndex: 'sourceResourceName',
          scopedSlots: { customRender: 'sourceResourceName' }
        },
        {
          title: '目的资源',
          dataIndex: 'destResourceName',
          scopedSlots: { customRender: 'destResourceName' }
        },
        {
          title: '启动',
          dataIndex: 'enable',
          scopedSlots: { customRender: 'enable' }
        },
        {
          title: '操作',
          dataIndex: 'action',
          width: '150px',
          scopedSlots: { customRender: 'action' }
        }
      ],
      dataSource: [],
      queryParam: {},
      url: {
        list: '/api/rule/list',
        remove: '/api/rule/remove',
        update: '/api/rule/update',
        start: '/api/rule/start',
        stop: '/api/rule/stop',
        runtime: '/api/rule/runtime'
      },
     // transformModeMap: transformModeMap
    }
  },
  mounted() {
    this.loadData()
  },
  methods: {
    handleAdd() {
      this.$router.push({ path: '/rule/info/new' })
    },
    handleEdit(record) {
      this.$router.push({ path: '/rule/info/' + record.ruleId })
    },
    handleRuntime(record) {
      //this.$router.push({ name: 'ruleRuntime', params: { ruleId: record.ruleId } })
      this.$router.push({ path: '/rule/runtime/' + record.ruleId })
    },
    loadData() {
      this.loading = true
      postAction(this.url.list, this.queryParam).then(res => {
        this.dataSource = res.data
        this.loading = false
      })
    },
    handleStart(item) {
      postAction(this.url.start, item).then(res => {
        if (res.code === 200) {
          this.$message.success('启动成功')
          this.loadData()
        } else {
          this.$message.error('启动失败')
        }
      })
    },
    handleStop(item) {
      this.$confirm({
        title: '停止此规则?',
        content: item.ruleName,
        onOk: () => {
          postAction(this.url.stop, item).then(res => {
            if (res.code === 200) {
              this.$message.success('停止成功')
              this.loadData()
            } else {
              this.$message.error('停止失败')
            }
          })
        }
      })
    },
    handleDelete(item) {
      this.$confirm({
        title: '删除此规则?',
        content: item.ruleName,
        okType: 'danger',
        onOk: () => {
          postAction(this.url.remove, item).then(res => {
            if (res.code === 200) {
              this.$message.success('删除成功')
              this.loadData()
            } else {
              this.$message.error('删除失败')
            }
          })
        }
      })
    },
    enableChange(item) {
      if (item.enable) {
        this.handleStop(item)
      } else {
        this.handleStart(item)
      }
    },
    getResourceNameStr(destList) {
      let str = []
      for (let i = destList.length - 1; i >= 0; i--) {
        str.push(destList[i].resourceName)
      }
      return str.join(',')
    },
    reset() {
      this.queryParam = {}
      this.loadData()
    }
  }
}
</script>

