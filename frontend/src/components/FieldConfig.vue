<!-- 字段配置组件 -->
<template>
  <el-dialog
    v-model="dialogVisible"
    title="字段配置"
    width="800px"
  >
    <el-table :data="localFields" border style="width: 100%">
      <el-table-column label="字段名称" prop="label" width="150" />
      <el-table-column label="标识符" prop="prop" width="150" />
      <el-table-column label="类型" prop="type" width="120">
        <template #default="{ row }">
          <el-select v-model="row.type" style="width: 100%">
            <el-option label="输入框" value="input" />
            <el-option label="文本域" value="textarea" />
            <el-option label="选择框" value="select" />
            <el-option label="日期" value="date" />
          </el-select>
        </template>
      </el-table-column>
      <el-table-column label="宽度" width="100">
        <template #default="{ row }">
          <el-input-number v-model="row.width" :min="50" :max="500" :step="10" />
        </template>
      </el-table-column>
      <el-table-column label="显示" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.visible" />
        </template>
      </el-table-column>
      <el-table-column label="必填" width="80">
        <template #default="{ row }">
          <el-switch v-model="row.required" />
        </template>
      </el-table-column>
      <el-table-column label="选项配置" width="120">
        <template #default="{ row }">
          <el-button 
            type="primary" 
            link
            :disabled="row.type !== 'select'"
            @click="showOptionsConfig(row)"
          >
            配置选项
          </el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100">
        <template #default="{ row }">
          <el-button type="danger" link @click="handleDeleteField(row)">
            删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <div class="dialog-footer">
      <el-button @click="handleCancel">取消</el-button>
      <el-button type="primary" @click="handleSave">
        确定
      </el-button>
    </div>
  </el-dialog>

  <!-- 选项配置对话框 -->
  <el-dialog
    v-model="optionsDialogVisible"
    title="选项配置"
    width="500px"
    append-to-body
  >
    <div v-if="currentField">
      <div class="options-header">
        <el-button type="primary" @click="addOption">添加选项</el-button>
      </div>
      <el-table :data="currentField.options || []" border style="width: 100%">
        <el-table-column label="标签" prop="label">
          <template #default="{ row }">
            <el-input v-model="row.label" placeholder="请输入标签" />
          </template>
        </el-table-column>
        <el-table-column label="值" prop="value">
          <template #default="{ row }">
            <el-input v-model="row.value" placeholder="请输入值" />
          </template>
        </el-table-column>
        <el-table-column label="操作" width="100">
          <template #default="{ $index }">
            <el-button type="danger" link @click="removeOption($index)">
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
      <div class="dialog-footer">
        <el-button @click="optionsDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveOptions">
          确定
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, watch } from 'vue'

const emit = defineEmits(['update:modelValue', 'save'])

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  fields: {
    type: Array,
    required: true
  }
})

const dialogVisible = ref(false)
const optionsDialogVisible = ref(false)
const localFields = ref([])
const currentField = ref(null)

// 监听对话框显示状态
watch(() => props.modelValue, (val) => {
  dialogVisible.value = val
  if (val) {
    localFields.value = JSON.parse(JSON.stringify(props.fields))
  }
})

watch(() => dialogVisible.value, (val) => {
  emit('update:modelValue', val)
})

// 显示选项配置
const showOptionsConfig = (field) => {
  currentField.value = field
  if (!currentField.value.options) {
    currentField.value.options = []
  }
  optionsDialogVisible.value = true
}

// 添加选项
const addOption = () => {
  if (!currentField.value.options) {
    currentField.value.options = []
  }
  currentField.value.options.push({
    label: '',
    value: ''
  })
}

// 删除选项
const removeOption = (index) => {
  currentField.value.options.splice(index, 1)
}

// 保存选项
const saveOptions = () => {
  optionsDialogVisible.value = false
}

// 删除字段
const handleDeleteField = (row) => {
  const index = localFields.value.findIndex(field => field.prop === row.prop)
  if (index > -1) {
    localFields.value.splice(index, 1)
  }
}

// 取消
const handleCancel = () => {
  dialogVisible.value = false
}

// 保存
const handleSave = () => {
  emit('save', localFields.value)
  dialogVisible.value = false
}
</script>

<style scoped>
.dialog-footer {
  display: flex;
  justify-content: flex-end;
  margin-top: 20px;
  gap: 10px;
}

.options-header {
  margin-bottom: 20px;
}
</style> 