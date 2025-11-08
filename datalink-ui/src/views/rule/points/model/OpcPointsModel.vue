<template>
  <div>
    <div style='width: 100%;padding-bottom: 10px' v-show='!disableEdit'>
      <a-button type='primary' @click='newItem'>添加</a-button>
    </div>

    <a-table
      :columns='columns'
      :dataSource='data'
      :pagination='false'
      :loading='loading'
      size='small'
      style='border: 1px #e8e3e3 solid'
    >
      <template v-for="(col, i) in ['namespace', 'tag']" :slot='col' slot-scope='text, record'>
        <a-input
          :key='col'
          v-if='record.editable'
          style='margin: -5px 0'
          :value='text'
          :placeholder='columns[i].title'
          @change='e => handleChange(e.target.value, record.key, col)'
        />
        <template v-else>{{ text }}</template>
      </template>

      <template slot='action' slot-scope='text, record'>
        <template v-if='record.editable'>
                      <span v-if='record.isNew'>
                        <a @click='saveRow(record)'>确定</a>
                        <a-divider type='vertical' />
                        <a-popconfirm title='删除此点位？' @confirm='remove(record.key)'>
                          <a>删除</a>
                        </a-popconfirm>
                      </span>
          <span v-else>
                        <a @click='saveRow(record)'>保存</a>
                        <a-divider type='vertical' />
                        <a @click='cancel(record.key)'>取消</a>
                    </span>
        </template>
        <span v-else>
                      <a @click='toggle(record.key)'>编辑</a>
                      <a-divider type='vertical' />
                      <a-popconfirm title='删除此点位？' @confirm='remove(record.key)'>
                        <a>删除</a>
                      </a-popconfirm>
                    </span>
      </template>
    </a-table>
  </div>
</template>

<script>


export default {
  name: 'OpcPointsModel',
  props: {
    disableEdit: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      loading: false,
      data: [],
      columns: [
        {
          title: '命名空间',
          dataIndex: 'namespace',
          key: 'namespace',
          width: '40%',
          scopedSlots: { customRender: 'namespace' }
        },
        {
          title: '标识',
          dataIndex: 'tag',
          key: 'tag',
          width: '40%',
          scopedSlots: { customRender: 'tag' }
        }
      ]
    }
  },
  mounted() {
    if (!this.disableEdit) {
      this.columns.push({
        title: '操作',
        key: 'action',
        scopedSlots: { customRender: 'action' }
      })
    }
  },
  methods: {
    set(points) {
      this.data = []
      if (!points) return
      let index = 1
      for (let point of points) {
        this.data.push({
          key: index++,
          namespace: point.namespace,
          tag: point.tag,
          editable: false,
          isNew: false
        })
      }
    },
    get() {
      let result = []
      for (let item of this.data) {
        if (item.isNew) continue
        result.push({
          namespace: item.namespace,
          tag: item.tag
        })
      }
      return result
    },
    newItem() {
      const length = this.data.length
      this.data.push({
        key: length === 0 ? '1' : (parseInt(this.data[length - 1].key) + 1).toString(),
        namespace: 0,
        tag: '',
        editable: true,
        isNew: true
      })
    },
    remove(key) {
      this.data = this.data.filter(item => item.key !== key)
    },
    saveRow(record) {
      this.loading = true
      const { key, namespace, tag } = record
      if (!namespace || !tag) {
        this.loading = false
        this.$message.error('填写不完整')
        return
      }
      const target = this.data.find(item => item.key === key)
      target.editable = false
      target.isNew = false
      this.loading = false
    },
    toggle(key) {
      const target = this.data.find(item => item.key === key)
      target._originalData = { ...target }
      target.editable = !target.editable
    },
    cancel(key) {
      const target = this.data.find(item => item.key === key)
      Object.keys(target).forEach(key => {
        target[key] = target._originalData[key]
      })
      target._originalData = undefined
    },
    handleChange(value, key, column) {
      const newData = [...this.data]
      const target = newData.find(item => key === item.key)
      if (target) {
        target[column] = value
        this.data = newData
      }
    }

  }
}
</script>

