<template>
  <div :class='wrpCls'>
    <avatar-dropdown :menu='showMenu' :current-user='currentUser' :class='prefixCls' @updatePassword='updatePassword' />
<!--    <select-lang :class='prefixCls' />-->
    <password-model ref='PasswordModel'></password-model>
  </div>
</template>

<script>
import AvatarDropdown from './AvatarDropdown'
import SelectLang from '@/components/SelectLang'
import PasswordModel from '../../views/user/PasswordModel'

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
      currentUser: {}
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
  },
  methods:{
    updatePassword(){
      this.$refs.PasswordModel.open()
    }
  }
}
</script>
