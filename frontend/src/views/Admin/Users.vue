<template>
  <div>
    <el-card class="search-card">
      <el-input 
        v-model="searchUsername" 
        placeholder="输入用户名查询" 
        style="width: 300px;"
        @keyup.enter="handleSearch"
      >
        <template #append>
          <el-button @click="handleSearch">搜索</el-button>
        </template>
      </el-input>
    </el-card>

    <el-card class="table-card">
      <template #header>
        <div class="card-header">
          <span>用户管理</span>
          <el-button type="primary" @click="openAddModal">新增用户</el-button>
        </div>
      </template>
      <el-table :data="filteredUsers" stripe>
        <el-table-column prop="id" label="ID" width="60" />
        <el-table-column prop="username" label="用户名" width="120" />
        <el-table-column prop="phone" label="联系方式" width="140" />
        <el-table-column prop="state" label="用户身份" width="100">
          <template #default="{ row }">
            <el-tag :type="row.state === 1 ? 'danger' : 'primary'">
              {{ row.state === 1 ? '管理员' : '普通用户' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="country" label="所在国家" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openEditModal(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog v-model="showModal" :title="isEdit ? '编辑用户' : '新增用户'" width="500px">
      <el-form :model="form" label-width="100px">
        <el-form-item label="用户名">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="form.phone" placeholder="请输入电话" />
        </el-form-item>
        <el-form-item v-if="!isEdit" label="密码">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="用户身份">
          <el-select v-model="form.state" style="width: 100%;">
            <el-option label="普通用户" :value="0" />
            <el-option label="管理员" :value="1" />
          </el-select>
        </el-form-item>
        <el-form-item label="所在国家">
          <el-input v-model="form.country" placeholder="请输入所在国家" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showModal = false">取消</el-button>
        <el-button type="primary" @click="handleSave">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getUsers, addUser, updateUser, deleteUser } from '@/api/user'

const showModal = ref(false)
const isEdit = ref(false)
const searchUsername = ref('')

const users = ref([])

const fetchData = async () => {
  try {
    const res = await getUsers()
    users.value = res.records || res || []
  } catch {
    ElMessage.error('获取用户列表失败')
  }
}

onMounted(() => {
  fetchData()
})

const filteredUsers = computed(() => {
  if (!searchUsername.value) return users.value
  return users.value.filter(item => item.username.includes(searchUsername.value))
})

const handleSearch = () => {
  if (searchUsername.value) {
    const found = users.value.find(item => item.username.includes(searchUsername.value))
    if (!found) {
      ElMessage.warning('未找到该用户')
    }
  }
}

const form = reactive({
  id: null,
  username: '',
  phone: '',
  password: '',
  state: 0,
  country: ''
})

const resetForm = () => {
  form.id = null
  form.username = ''
  form.phone = ''
  form.password = ''
  form.state = 0
  form.country = ''
}

const openAddModal = () => {
  isEdit.value = false
  resetForm()
  showModal.value = true
}

const openEditModal = (row) => {
  isEdit.value = true
  form.id = row.id
  form.username = row.username
  form.phone = row.phone
  form.password = ''
  form.state = row.state
  form.country = row.country
  showModal.value = true
}

const handleSave = async () => {
  if (!form.username || !form.phone) {
    ElMessage.warning('请填写完整信息')
    return
  }
  if (!isEdit.value && !form.password) {
    ElMessage.warning('请输入密码')
    return
  }
  try {
    if (isEdit.value) {
      await updateUser({ id: form.id, username: form.username, phone: form.phone, state: form.state, country: form.country })
      ElMessage.success('用户信息已更新')
    } else {
      await addUser({ username: form.username, phone: form.phone, password: form.password, state: form.state, country: form.country })
      ElMessage.success('用户已创建')
    }
    showModal.value = false
    fetchData()
  } catch {
    ElMessage.error('操作失败')
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定要删除用户 "${row.username}" 吗？`, '确认删除', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser(row.id)
      ElMessage.success('用户已删除')
      fetchData()
    } catch {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
}

.table-card {
  margin-bottom: 20px;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
