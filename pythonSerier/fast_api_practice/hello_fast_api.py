from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI()

profile = {
    'hello' : '关于我',
    'helloSubTitle':'项目，创意'
}

@app.get('/api/profile')
def get_profile():
    return profile


class WeatherRequest(BaseModel):
    """
    BaseModel 提供校验、类型转换、自动 __init__ 等功能，同时它是 FastAPI识别"这是请求体模型"的依据。不继承就只是个普通类，注解不生效，FastAPI 也不会从 body 解析它。
    """
    area:str

@app.post('/api/post')
# def post_weather(area:str) -> dict :  # 表单形式
def post_weather(req:WeatherRequest) -> dict :  # json形式
    area = req.area
    if area =='北京':
        return {
            "area":area,
            "weather": "晴天",
            "temperature":"38℃"
        }
    elif area == '上海':
        return {
            "area": area,
            "weather": "雨天",
            "temperature": "338℃"
        }
    return {
        "area": area,
        "weather": "服务器繁忙，稍后重试",
        "temperature": "服务器繁忙，稍后重试"
    }
