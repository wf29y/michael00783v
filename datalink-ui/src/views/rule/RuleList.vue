<template>
  <page-header-wrapper>
    <a-card hoverable :bordered='false'>
      <a-list
        :grid='{ gutter: 24, lg: 3, md: 1, sm: 1, xs: 1 }'
        :loading='loading'
        :data-source='data'
      >
        <a-list-item slot='renderItem' slot-scope='item'>
          <template v-if='!item || item.ruleId === undefined'>
            <a-button @click='handleAdd()' class='new-btn' type='dashed' style='height: 194px'>
              <a-icon type='plus' />
              新增传输规则
            </a-button>
          </template>
          <template v-else>
            <a-card>
              <div slot='title'>{{ item.ruleName }}</div>
              <div slot='extra'>
                <a-badge v-if='item.enable' color='green' text='运行中' />
                <a-badge v-if='!item.enable' color='black' text='未启动' />
              </div>
              <a-row :gutter='16'>
                <a-col :span='7'>
                  <div>源数据：</div>
                </a-col>
                <a-col :span='12'>
                  <div>{{ item.sourceResource.resourceName }}</div>
                </a-col>
              </a-row>
              <a-row :gutter='16'>
                <a-col :span='7'>
                  <div>目的：</div>
                </a-col>
                <a-col :span='12'>
                  <div>{{ item.destResourceList.length }}</div>
                </a-col>
              </a-row>

              <span slot='actions'>
                 <a v-if='!item.enable' @click='handleStart(item)'>启动</a>
                 <a v-if='item.enable' @click='handleStop(item)'>停止</a>
              </span>
              <a slot='actions' @click='handleEdit(item)' v-if='!item.enable'>编辑</a>
              <a slot='actions' @click='handleInfo(item)' v-if='item.enable'>状态</a>
              <a-popconfirm slot='actions' title='确定删除此规则?' @confirm='() => handleDelete(item)' v-if='!item.enable'>
                <a href='javascript:;'>删除</a>
              </a-popconfirm>
            </a-card>
          </template>
        </a-list-item>
      </a-list>
    </a-card>
    <runtime-model ref='RuntimeModel'></runtime-model>
  </page-header-wrapper>
</template>

<script>
import { postAction, getAction } from '@/api/manage'
import RuntimeModel from './modules/RuntimeModel'

export default {
  name: 'RuleList',
  components: { RuntimeModel },
  data() {
    return {
      loading: true,
      data: [],
      url: {
        list: '/api/rule/list',
        remove: '/api/rule/remove',
        update: '/api/rule/update',
        start: '/api/rule/start',
        stop: '/api/rule/stop',
        runtime: '/api/rule/runtime'
      }
    }
  },
  created() {
    this.loadData()
  },
  methods: {
    handleAdd() {
      this.$router.push({ name: 'ruleInfo', params: { ruleId: undefined } })
    },
    handleEdit(record) {
      this.$router.push({ name: 'ruleInfo', params: { ruleId: record.ruleId } })
    },
    loadData() {
      this.loading = true
      postAction(this.url.list, {}).then(res => {
        this.data = res.data
        this.data.unshift({})
        this.loading = false
      })
    },
    handleStart(item) {
      postAction(this.url.start, item).then(res => {
        if (res.code === 200) {
          this.$message.success('启动成功')
          this.loadData()
        } else {
          this.$message.info(res.message)
        }
      })
    },
    handleStop(item) {
      postAction(this.url.stop, item).then(res => {
        if (res.code === 200) {
          this.$message.success('停止成功')
          this.loadData()
        } else {
          this.$message.info(res.message)
        }
      })
    },
    handleDelete(item) {
      postAction(this.url.remove, item).then(res => {
        if (res.code === 200) {
          this.$message.success('删除成功')
          this.loadData()
        } else {
          this.$message.info(res.message)
        }
      })
    },
    handleInfo(item) {
      this.$refs.RuntimeModel.show(item.ruleId)
    }
  }
}
</script>

<style lang='less' scoped>
@import '~@/components/index.less';

.card-list {
  /deep/ .ant-card-body:hover {
    .ant-card-meta-title > a {
      color: @primary-color;
    }
  }

  /deep/ .ant-card-meta-title {
    margin-bottom: 12px;

    & > a {
      display: inline-block;
      max-width: 100%;
      color: rgba(0, 0, 0, 0.85);
    }
  }

  /deep/ .meta-content {
    position: relative;
    overflow: hidden;
    text-overflow: ellipsis;
    display: -webkit-box;
    height: 64px;
    -webkit-line-clamp: 3;
    -webkit-box-orient: vertical;

    margin-bottom: 1em;
  }
}

.card-avatar {
  width: 48px;
  height: 48px;
  border-radius: 48px;
}

.ant-card-actions {
  background: #f7f9fa;

  li {
    float: left;
    text-align: center;
    margin: 12px 0;
    color: rgba(0, 0, 0, 0.45);
    width: 50%;

    &:not(:last-child) {
      border-right: 1px solid #e8e8e8;
    }

    a {
      color: rgba(0, 0, 0, 0.45);
      line-height: 22px;
      display: inline-block;
      width: 100%;

      &:hover {
        color: @primary-color;
      }
    }
  }
}

.new-btn {
  background-color: #fff;
  border-radius: 2px;
  width: 100%;
  height: 188px;
}
</style>
