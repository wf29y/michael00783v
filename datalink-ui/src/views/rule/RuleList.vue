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

      <a-list
        :grid='{ gutter: 24, lg: 4, md: 2, sm: 1, xs: 1 }'
        :loading='loading'
        :data-source='dataSource'
      >
        <a-list-item slot='renderItem' slot-scope='item'>
          <a-card>
            <div slot='title' style='height: 23px'>{{ item.ruleName }}</div>
            <a-tag slot='extra' color='green' v-show='item.enable'>运行中</a-tag>
            <a-row>
              <a-col :span='6'>
                <div>数据源：</div>
              </a-col>
              <a-col :span='18'>
                <div>{{ truncateString(getResourceNameStr(item.sourceResourceList), 20) }}</div>
              </a-col>
            </a-row>
            <a-row>
              <a-col :span='6'>
                <div>目的资源：</div>
              </a-col>
              <a-col :span='18'>
                <div>{{ truncateString(getResourceNameStr(item.destResourceList), 20) }}</div>
              </a-col>
            </a-row>
            <a slot='actions' @click='handleRuntime(item)'>详情</a>
            <a slot='actions' @click='handleEdit(item)' v-if='!item.enable'>配置</a>
            <a-popconfirm slot='actions' title='确定删除此规则?' @confirm='() => handleDelete(item)'>
              <a href='javascript:;'>删除</a>
            </a-popconfirm>
            <a slot='actions' v-if='item.enable' @click='enableChange(item)'>停止</a>
            <a slot='actions' v-else @click='enableChange(item)'>启动</a>
          </a-card>
        </a-list-item>
      </a-list>

    </a-card>
  </page-header-wrapper>
</template>

<script>
import { postAction } from '@/api/manage'
import { truncateString } from '@/utils/util'

export default {
  name: 'RuleList',
  components: {},
  data() {
    return {
      loading: true,
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
      truncateString: truncateString
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

