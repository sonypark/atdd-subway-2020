import ApiService from '@/api'

const BASE_URL = '/paths'

const PathService = {
  get() {
    return ApiService.get(BASE_URL)
  }
}

export default PathService
