import request from './request'

// 用户
export const login = (data) => request.post('/user/login', data)
export const getUserPage = (params) => request.get('/user/page', { params })
export const addUser = (data) => request.post('/user', data)
export const updateUser = (data) => request.put('/user', data)
export const deleteUser = (id) => request.delete('/user/' + id)
export const getUserById = (id) => request.get('/user/' + id)

// 角色
export const getRoleList = () => request.get('/role/list')
export const addRole = (data) => request.post('/role', data)
export const updateRole = (data) => request.put('/role', data)
export const deleteRole = (id) => request.delete('/role/' + id)

// 菜单
export const getMenuList = () => request.get('/menu/list')
export const getMenuByRole = (roleId) => request.get('/menu/role/' + roleId)
export const addMenu = (data) => request.post('/menu', data)
export const updateMenu = (data) => request.put('/menu', data)
export const deleteMenu = (id) => request.delete('/menu/' + id)

// 客户
export const getCustomerPage = (params) => request.get('/customer/page', { params })
export const getCustomerList = () => request.get('/customer/list')
export const addCustomer = (data) => request.post('/customer', data)
export const updateCustomer = (data) => request.put('/customer', data)
export const deleteCustomer = (id) => request.delete('/customer/' + id)
export const getCustomerById = (id) => request.get('/customer/' + id)

// 床位
export const getBedCount = (roomNo) => request.get('/bed/count', { params: { roomNo } })
export const getBedList = () => request.get('/bed/list')
export const addBed = (data) => request.post('/bed', data)
export const updateBed = (data) => request.put('/bed', data)
export const deleteBed = (id) => request.delete('/bed/' + id)
export const getBedById = (id) => request.get('/bed/' + id)
export const updateBedStatus = (data) => request.put('/bed/status', data)

// 房间
export const getRoomList = () => request.get('/room/list')
export const addRoom = (data) => request.post('/room', data)
export const updateRoom = (data) => request.put('/room', data)
export const deleteRoom = (id) => request.delete('/room/' + id)

// 菜品
export const getFoodList = () => request.get('/food/list')
export const addFood = (data) => request.post('/food', data)
export const updateFood = (data) => request.put('/food', data)
export const deleteFood = (id) => request.delete('/food/' + id)

// 套餐
export const getMealPage = (params) => request.get('/meal/page', { params })
export const addMeal = (data) => request.post('/meal', data)
export const updateMeal = (data) => request.put('/meal', data)
export const deleteMeal = (id) => request.delete('/meal/' + id)

// 护理等级
export const getNurseLevelList = () => request.get('/nurselevel/list')
export const addNurseLevel = (data) => request.post('/nurselevel', data)
export const updateNurseLevel = (data) => request.put('/nurselevel', data)
export const deleteNurseLevel = (id) => request.delete('/nurselevel/' + id)

// 护理内容
export const getNurseContentList = () => request.get('/nursecontent/list')
export const addNurseContent = (data) => request.post('/nursecontent', data)
export const updateNurseContent = (data) => request.put('/nursecontent', data)
export const deleteNurseContent = (id) => request.delete('/nursecontent/' + id)

// 护理记录
export const getNurseRecordPage = (params) => request.get('/nurserecord/page', { params })
export const addNurseRecord = (data) => request.post('/nurserecord', data)
export const updateNurseRecord = (data) => request.put('/nurserecord', data)
export const deleteNurseRecord = (id) => request.delete('/nurserecord/' + id)

// 外出
export const getOutwardPage = (params) => request.get('/outward/page', { params })
export const addOutward = (data) => request.post('/outward', data)
export const updateOutward = (data) => request.put('/outward', data)
export const deleteOutward = (id) => request.delete('/outward/' + id)

// 退住
export const getBackdownPage = (params) => request.get('/backdown/page', { params })
export const addBackdown = (data) => request.post('/backdown', data)
export const updateBackdown = (data) => request.put('/backdown', data)
export const deleteBackdown = (id) => request.delete('/backdown/' + id)

// 点餐
export const getFoodOrderPage = (params) => request.get('/foodorder/page', { params })
export const addFoodOrder = (data) => request.post('/foodorder', data)
export const updateFoodOrder = (data) => request.put('/foodorder', data)
export const deleteFoodOrder = (id) => request.delete('/foodorder/' + id)

// 呼叫
export const getCallRecordPage = (params) => request.get('/callrecord/page', { params })
export const addCallRecord = (data) => request.post('/callrecord', data)
export const respondCall = (id) => request.put('/callrecord/' + id + '/respond')
export const deleteCallRecord = (id) => request.delete('/callrecord/' + id)
