// OpenAI 流式对话客户端
const OpenAI = require('openai');

async function* streamChat(systemPrompt, history, userMessage) {
  const client = new OpenAI({
    apiKey: process.env.LLM_API_KEY,
    baseURL: process.env.LLM_BASE_URL,
  });
  const stream = await client.chat.completions.create({
    model: process.env.LLM_MODEL,
    messages: [
      { role: 'system', content: systemPrompt },
      ...history,
      { role: 'user', content: userMessage },
    ],
    stream: true,
    max_tokens: 2048,
    temperature: 0.7,
  });
  for await (const chunk of stream) {
    const delta = chunk.choices?.[0]?.delta?.content;
    if (delta) yield delta;
  }
}

module.exports = { streamChat };
