<template>
  <a-card title='环境变量' :body-style='{minHeight:"257px",paddingTop:"10px"}' :bordered='false'>

    <div slot='extra' style='padding: 0'>
      <a @click='newItem'>添加</a>
    </div>

    <a-table
      :columns='columns'
      :dataSource='data'
      :pagination='false'
      :loading='loading'
      size='middle'
    >
      <template v-for="(col, i) in ['name', 'value']" :slot='col' slot-scope='text, record'>
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
                        <a-popconfirm title='删除此变量？' @confirm='remove(record.key)'>
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
                      <a-popconfirm title='删除此变量？' @confirm='remove(record.key)'>
                        <a>删除</a>
                      </a-popconfirm>
                    </span>
      </template>
    </a-table>
  </a-card>
</template>

<script>


export default {
  name: 'VariablesModel',
  data() {
    return {
      loading: false,
      data: [],
      columns: [
        {
          title: '变量名',
          dataIndex: 'name',
          key: 'name',
          width: '40%',
          scopedSlots: { customRender: 'name' }
        },
        {
          title: '初始值',
          dataIndex: 'value',
          key: 'value',
          width: '40%',
          scopedSlots: { customRender: 'value' }
        },
        {
          title: '操作',
          key: 'action',
          scopedSlots: { customRender: 'action' }
        }
      ]
    }
  },
  methods: {
    set(variables) {
      this.data = []
      if (!variables) return
      let keys = Object.keys(variables)
      for (let key of keys) {
        this.data.push({
          name: key,
          value: variables[key]
        })
      }
    },
    get() {
      let result = {}
      for (let item of this.data) {
        result[item.name] = item.value
      }
      return result
    },
    newItem() {
      const length = this.data.length
      this.data.push({
        key: length === 0 ? '1' : (parseInt(this.data[length - 1].key) + 1).toString(),
        name: '',
        value: '',
        editable: true,
        isNew: true
      })
    },
    remove(key) {
      this.data = this.data.filter(item => item.key !== key)
    },
    saveRow(record) {
      this.loading = true
      const { key, name, value } = record
      if (!name || !value) {
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

<style>


</style>
