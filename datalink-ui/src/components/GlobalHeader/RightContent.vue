<template>
  <div :class='wrpCls'>

    <span style='font-size: 14px;display: inline-block;vertical-align: middle;margin-right: 20px'><span
      style='font-weight: bold'>时间：</span>{{ time }}</span>
    <span style='font-size: 14px;display: inline-block;vertical-align: middle;margin-right: 4px'><span
      style='font-weight: bold'>版本：</span>{{ version }}</span>

    <avatar-dropdown :menu='showMenu' :current-user='currentUser' :class='prefixCls' @updatePassword='updatePassword' />
    <!--    <select-lang :class='prefixCls' />-->
    <password-model ref='PasswordModel'></password-model>
  </div>
</template>

<script>
import AvatarDropdown from './AvatarDropdown'
import SelectLang from '@/components/SelectLang'
import PasswordModel from '../../views/user/PasswordModel'
import { getAction } from '@/api/manage'

export default {
  name: 'RightContent',
  components: {
    AvatarDropdown,
    SelectLang,
    PasswordModel
  },
  props: {
    prefixCls: {
      type: String,
      default: 'ant-pro-global-header-index-action'
    },
    isMobile: {
      type: Boolean,
      default: () => false
    },
    topMenu: {
      type: Boolean,
      required: true
    },
    theme: {
      type: String,
      required: true
    }
  },
  data() {
    return {
      showMenu: true,
      currentUser: {},
      time: '—',
      version: '—'
    }
  },
  computed: {
    wrpCls() {
      return {
        'ant-pro-global-header-index-right': true,
        [`ant-pro-global-header-index-${this.isMobile || !this.topMenu ? 'light' : this.theme}`]: true
      }
    }
  },
  mounted() {
    setTimeout(() => {
      this.currentUser = {
        name: 'datalink'
      }
    }, 800)
    this.getTime()
    this.getVersion()
    setInterval(() => {
      this.getTime()
    }, 1000 * 60)
  },
  methods: {
    updatePassword() {
      this.$refs.PasswordModel.open()
    },
    getTime() {
      getAction('/api/system/time', {}).then((res) => {
        this.time = res
      })
    },
    getVersion() {
      getAction('/api/system/version', {}).then((res) => {
        this.version = res
      })
    }
  }
}
</script>
