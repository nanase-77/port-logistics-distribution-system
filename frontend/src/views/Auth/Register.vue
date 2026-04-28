<template>
  <div class="register-container">
    <el-card class="register-card">
      <template #header>
        <div class="card-header">
          <span>用户注册</span>
        </div>
      </template>
      <el-form ref="registerFormRef" :model="registerForm" :rules="registerRules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="registerForm.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input v-model="registerForm.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="registerForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="国家" prop="country">
          <div class="country-select-trigger" @click="showCountryDialog = true">
            <span class="selected-value">{{ registerForm.country || '请选择所在国家' }}</span>
            <span class="arrow">▼</span>
          </div>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="handleRegister">
            注册
          </el-button>
        </el-form-item>
        <div class="link-container">
          <span>已有账号？</span>
          <router-link to="/login">立即登录</router-link>
        </div>
      </el-form>
    </el-card>

    <!-- 国家选择弹窗 -->
    <div v-if="showCountryDialog" class="country-modal-overlay" @click="handleOverlayClick">
      <div class="country-modal" @click.stop @mouseenter="handlePopoverMouseEnter" @mouseleave="handlePopoverMouseLeave">
        <div class="modal-header">
          <span>选择国家及地区</span>
          <button class="close-btn" @click="showCountryDialog = false">×</button>
        </div>
        <div class="modal-body">
          <!-- 字母导航标签 -->
          <div class="country-tabs">
            <div 
              v-for="tab in countryTabs" 
              :key="tab.key"
              :class="['tab-item', { active: activeTab === tab.key }]"
              @click="activeTab = tab.key"
            >
              {{ tab.label }}
            </div>
          </div>

          <!-- 国家列表 -->
          <div class="country-list">
            <div 
              v-for="country in filteredCountries" 
              :key="country"
              :class="['country-item', { selected: selectedCountry === country }]"
              @click="selectCountry(country)"
            >
              {{ country }}
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <el-button type="primary" @click="confirmCountry">确定</el-button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { register } from '@/api/user'

const router = useRouter()
const registerFormRef = ref()
const loading = ref(false)
const showCountryDialog = ref(false)
const activeTab = ref('hot')
const selectedCountry = ref('')
const leaveTimer = ref(null)

const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: '',
  phone: '',
  country: ''
})

// 国家数据
const countryData = {
  hot: ['中国大陆', '中国香港', '中国澳门', '中国台湾', '新加坡', '马来西亚', '澳大利亚', '新西兰', '美国', '加拿大', '日本'],
  ABCDEF: ['阿富汗', '阿尔巴尼亚', '阿尔及利亚', '安道尔', '安哥拉', '安提瓜和巴布达', '阿根廷', '亚美尼亚', '澳大利亚', '奥地利', '阿塞拜疆', '巴哈马', '巴林', '孟加拉国', '巴巴多斯', '白俄罗斯', '比利时', '伯利兹', '贝宁', '不丹', '玻利维亚', '波斯尼亚和黑塞哥维那', '博茨瓦纳', '巴西', '文莱', '保加利亚', '布基纳法索', '布隆迪', '柬埔寨', '喀麦隆', '加拿大', '佛得角', '中非共和国', '乍得', '智利', '中国', '哥伦比亚', '科摩罗', '刚果', '哥斯达黎加', '科特迪瓦', '克罗地亚', '古巴', '塞浦路斯', '捷克', '丹麦', '吉布提', '多米尼克', '多米尼加', '厄瓜多尔', '埃及', '萨尔瓦多', '赤道几内亚', '厄立特里亚', '爱沙尼亚', '埃塞俄比亚', '斐济', '芬兰', '法国'],
  GHIJ: ['加蓬', '冈比亚', '格鲁吉亚', '德国', '加纳', '希腊', '格林纳达', '危地马拉', '几内亚', '几内亚比绍', '圭亚那', '海地', '洪都拉斯', '匈牙利', '冰岛', '印度', '印度尼西亚', '伊朗', '伊拉克', '爱尔兰', '以色列', '意大利', '牙买加', '日本'],
  KLMN: ['哈萨克斯坦', '肯尼亚', '基里巴斯', '朝鲜', '韩国', '科威特', '吉尔吉斯斯坦', '老挝', '拉脱维亚', '黎巴嫩', '莱索托', '利比里亚', '利比亚', '列支敦士登', '立陶宛', '卢森堡', '马达加斯加', '马拉维', '马来西亚', '马尔代夫', '马里', '马耳他', '马绍尔群岛', '毛里塔尼亚', '毛里求斯', '墨西哥', '密克罗尼西亚', '摩尔多瓦', '摩纳哥', '蒙古', '黑山', '摩洛哥', '莫桑比克', '缅甸', '纳米比亚', '瑙鲁', '尼泊尔', '荷兰', '新西兰', '尼加拉瓜', '尼日尔', '尼日利亚', '挪威'],
  PQRSTUV: ['巴基斯坦', '帕劳', '巴拿马', '巴布亚新几内亚', '巴拉圭', '秘鲁', '菲律宾', '波兰', '葡萄牙', '卡塔尔', '罗马尼亚', '俄罗斯', '卢旺达', '圣基茨和尼维斯', '圣卢西亚', '圣文森特和格林纳丁斯', '萨摩亚', '圣马力诺', '圣多美和普林西比', '沙特阿拉伯', '塞内加尔', '塞尔维亚', '塞舌尔', '塞拉利昂', '新加坡', '斯洛伐克', '斯洛文尼亚', '所罗门群岛', '索马里', '南非', '南苏丹', '西班牙', '斯里兰卡', '苏丹', '苏里南', '斯威士兰', '瑞典', '瑞士', '叙利亚', '塔吉克斯坦', '泰国', '东帝汶', '多哥', '汤加', '特立尼达和多巴哥', '突尼斯', '土耳其', '土库曼斯坦', '图瓦卢', '乌干达', '乌克兰', '阿联酋', '英国', '美国', '乌拉圭', '乌兹别克斯坦'],
  WXYZ: ['瓦努阿图', '委内瑞拉', '越南', '也门', '赞比亚', '津巴布韦']
}

// 标签页配置
const countryTabs = [
  { key: 'hot', label: '热门国家及地区' },
  { key: 'ABCDEF', label: 'ABCDEF' },
  { key: 'GHIJ', label: 'GHIJ' },
  { key: 'KLMN', label: 'KLMN' },
  { key: 'PQRSTUV', label: 'PQRSTUV' },
  { key: 'WXYZ', label: 'WXYZ' }
]

// 过滤后的国家列表
const filteredCountries = computed(() => {
  return countryData[activeTab.value] || []
})


// 选择国家
const selectCountry = (country) => {
  selectedCountry.value = country
}

// 确认选择
const confirmCountry = () => {
  if (selectedCountry.value) {
    registerForm.country = selectedCountry.value
    showCountryDialog.value = false
  } else {
    ElMessage.warning('请选择一个国家')
  }
}

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入密码不一致'))
  } else {
    callback()
  }
}

const registerRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在3到20个字符之间', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于6位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请再次输入密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号格式', trigger: 'blur' }
  ],
  country: [
    { required: true, message: '请输入所在国家', trigger: 'blur' }
  ]
}

const handleRegister = async () => {
  if (!registerFormRef.value) return
  await registerFormRef.value.validate(async (valid) => {
    if (valid) {
      loading.value = true
      try {
        const { confirmPassword, ...registerData } = registerForm
        await register(registerData)
        ElMessage.success('注册成功，请登录')
        router.push('/login')
      } catch (error) {
        console.error('注册失败:', error)
      } finally {
        loading.value = false
      }
    }
  })
}
</script>

<style scoped>
.register-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-image: url('./hsh.jpg');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
}

.register-card {
  width: 420px;
}

.card-header {
  text-align: center;
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.link-container {
  text-align: center;
  margin-top: 10px;
}

.link-container a {
  color: #409eff;
  text-decoration: none;
}

/* 国家选择触发区域样式 */
.country-select-trigger {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 15px;
  height: 40px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  background: white;
  cursor: pointer;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.country-select-trigger:hover {
  border-color: #c0c4cc;
  box-shadow: 0 0 2px rgba(0, 0, 0, 0.1);
}

.country-select-trigger:focus {
  border-color: #409eff;
  box-shadow: 0 0 0 2px rgba(64, 158, 255, 0.2);
  outline: none;
}

.selected-value {
  flex: 1;
  text-align: left;
  font-size: 14px;
  color: #606266;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.arrow {
  font-size: 12px;
  color: #c0c4cc;
  margin-left: 10px;
}

/* 国家选择弹窗样式 */
.country-select-wrapper {
  display: inline-block;
  width: 100%;
}

/* 弹窗遮罩层 */
.country-modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

/* 弹窗容器 */
.country-modal {
  background: white;
  border-radius: 8px;
  width: 600px;
  max-height: 500px;
  display: flex;
  flex-direction: column;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.15);
}

/* 弹窗头部 */
.modal-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: bold;
  font-size: 16px;
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  cursor: pointer;
  color: #909399;
  line-height: 1;
  padding: 0;
}

.close-btn:hover {
  color: #606266;
}

/* 弹窗内容 */
.modal-body {
  padding: 15px;
  overflow-y: auto;
  flex: 1;
}

/* 弹窗底部 */
.modal-footer {
  padding: 15px 20px;
  border-top: 1px solid #e4e7ed;
  text-align: right;
}

.country-dialog {
  padding: 10px;
}

.country-tabs {
  display: flex;
  border-bottom: 1px solid #e4e7ed;
  padding-bottom: 8px;
  margin-bottom: 15px;
}

.tab-item {
  padding: 6px 12px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  margin-right: 8px;
  border-radius: 4px;
  transition: all 0.2s;
}

.tab-item:hover {
  background-color: #f5f7fa;
}

.tab-item.active {
  color: #409eff;
  background-color: #e8f4fd;
}

.country-list {
  display: flex;
  flex-wrap: wrap;
  max-height: 300px;
  overflow-y: auto;
  padding-right: 10px;
}

.country-item {
  padding: 8px 16px;
  font-size: 14px;
  color: #606266;
  cursor: pointer;
  margin: 4px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  transition: all 0.2s;
  min-width: 100px;
  text-align: center;
}

.country-item:hover {
  border-color: #c0c4cc;
  background-color: #f5f7fa;
}

.country-item.selected {
  color: #fff;
  background-color: #409eff;
  border-color: #409eff;
}

.country-list::-webkit-scrollbar {
  width: 6px;
}

.country-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.country-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.country-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
