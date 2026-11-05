import { useState, useEffect } from 'react'
import { api } from './api'

const campos = ['nome', 'cpf', 'rg', 'endereco', 'profissao']

function Rendimentos({ value, onChange }) {
  const set = (i, k, v) => onChange(value.map((r, j) => (j === i ? { ...r, [k]: v } : r)))
  return (
    <div>
      <b>Rendimentos (máx. 3)</b>
      {value.map((r, i) => (
        <div key={i} style={{ display: 'flex', gap: 6, margin: '4px 0' }}>
          <input placeholder="Entidade empregadora" value={r.entidadeEmpregadora} onChange={(e) => set(i, 'entidadeEmpregadora', e.target.value)} />
          <input placeholder="Valor" type="number" value={r.valor} onChange={(e) => set(i, 'valor', e.target.value)} />
          <button type="button" onClick={() => onChange(value.filter((_, j) => j !== i))}>x</button>
        </div>))}
      {value.length < 3 && <button type="button" onClick={() => onChange([...value, { entidadeEmpregadora: '', valor: '' }])}>+ rendimento</button>}
    </div>)
}
const limpar = (r) => r.map((x) => ({ entidadeEmpregadora: x.entidadeEmpregadora, valor: Number(x.valor) || 0 }))

function Auth({ onLogin }) {
  const [modo, setModo] = useState('login')
  const [f, setF] = useState({ login: '', senha: '', perfil: 'CLIENTE', nome: '', cpf: '', rg: '', endereco: '', profissao: '' })
  const [rend, setRend] = useState([])
  const [erro, setErro] = useState('')
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })
  async function enviar(e) {
    e.preventDefault()
    try {
      const s = await api(modo === 'login' ? '/auth/login' : '/auth/register', { method: 'POST', body: { ...f, rendimentos: limpar(rend) } })
      localStorage.setItem('token', s.token); localStorage.setItem('perfil', s.perfil); onLogin(s.perfil)
    } catch (err) { setErro(err.message) }
  }
  return (
    <form onSubmit={enviar} style={{ display: 'grid', gap: 8, maxWidth: 360 }}>
      <h2>{modo === 'login' ? 'Entrar' : 'Cadastrar'}</h2>
      <input placeholder="Usuário" value={f.login} onChange={set('login')} required />
      <input placeholder="Senha" type="password" value={f.senha} onChange={set('senha')} required />
      {modo === 'cadastro' && <>
        <select value={f.perfil} onChange={set('perfil')}><option value="CLIENTE">Cliente</option><option value="EMPRESA">Agente - Empresa</option><option value="BANCO">Agente - Banco</option></select>
        {f.perfil === 'CLIENTE' && <>{campos.map((k) => <input key={k} placeholder={k} value={f[k]} onChange={set(k)} />)}<Rendimentos value={rend} onChange={setRend} /></>}
      </>}
      {erro && <p style={{ color: 'crimson' }}>{erro}</p>}
      <button>{modo === 'login' ? 'Entrar' : 'Cadastrar'}</button>
      <a href="#" onClick={(e) => { e.preventDefault(); setModo(modo === 'login' ? 'cadastro' : 'login') }}>{modo === 'login' ? 'Criar conta' : 'Já tenho conta'}</a>
    </form>)
}

const vazioPedido = { marca: '', modelo: '', ano: '', placa: '', matricula: '', proprietario: 'CLIENTE', dataInicio: '', dataFim: '' }

function Pedidos({ perfil }) {
  const [lista, setLista] = useState([])
  const [form, setForm] = useState(vazioPedido)
  const [editId, setEditId] = useState(null)
  const [erro, setErro] = useState('')
  const carregar = () => api('/pedidos').then(setLista).catch((e) => setErro(e.message))
  useEffect(() => { carregar() }, [])
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value })
  const agir = (fn) => async () => { try { await fn(); setErro(''); carregar() } catch (e) { setErro(e.message) } }
  const corpo = () => ({
    automovel: { marca: form.marca, modelo: form.modelo, ano: +form.ano, placa: form.placa, matricula: form.matricula, proprietario: form.proprietario },
    dataInicio: form.dataInicio, dataFim: form.dataFim })
  const salvar = agir(async () => {
    await api(editId ? `/pedidos/${editId}` : '/pedidos', { method: editId ? 'PUT' : 'POST', body: corpo() })
    setForm(vazioPedido); setEditId(null) })
  const editar = (p) => { setEditId(p.id); setForm({ ...p.automovel, ano: p.automovel.ano ?? '', dataInicio: p.dataInicio, dataFim: p.dataFim }) }
  const avaliar = (id, aprovado) => agir(() => api(`/pedidos/${id}/avaliar`, { method: 'PATCH', body: { aprovado, parecer: aprovado ? 'Parecer financeiro positivo' : 'Parecer financeiro negativo' } }))
  const contratar = (id) => agir(() => {
    const v = perfil === 'BANCO' ? prompt('Valor do crédito concedido (vazio = sem crédito)') : ''
    return api(`/pedidos/${id}/contratar`, { method: 'PATCH', body: { valorCredito: v ? Number(v) : null } }) })
  const mostrarForm = perfil === 'CLIENTE' || editId
  return (
    <div>
      <h2>Pedidos de aluguel</h2>
      {erro && <p style={{ color: 'crimson' }}>{erro}</p>}
      {mostrarForm && (
        <div style={{ display: 'flex', gap: 6, flexWrap: 'wrap', marginBottom: 16 }}>
          {['marca', 'modelo', 'ano', 'placa', 'matricula'].map((k) => <input key={k} placeholder={k} value={form[k]} onChange={set(k)} />)}
          <select value={form.proprietario} onChange={set('proprietario')}><option value="CLIENTE">Propriedade: cliente</option><option value="EMPRESA">Propriedade: empresa</option><option value="BANCO">Propriedade: banco</option></select>
          <input type="date" value={form.dataInicio} onChange={set('dataInicio')} /><input type="date" value={form.dataFim} onChange={set('dataFim')} />
          <button onClick={salvar}>{editId ? 'Salvar alterações' : 'Criar pedido'}</button>
          {editId && <button onClick={() => { setEditId(null); setForm(vazioPedido) }}>Cancelar edição</button>}
        </div>)}
      <table border="1" cellPadding="6"><thead><tr><th>#</th><th>Cliente</th><th>Automóvel</th><th>Proprietário</th><th>Período</th><th>Status</th><th>Crédito</th><th>Ações</th></tr></thead>
        <tbody>{lista.map((p) => (
          <tr key={p.id}><td>{p.id}</td><td>{p.cliente?.nome}</td>
            <td>{p.automovel?.marca} {p.automovel?.modelo} {p.automovel?.ano} ({p.automovel?.placa})</td><td>{p.automovel?.proprietario}</td>
            <td>{p.dataInicio} → {p.dataFim}</td><td>{p.status}{p.parecer ? ` — ${p.parecer}` : ''}</td><td>{p.contratoCredito?.valor ?? '-'}</td>
            <td>
              {p.status === 'PENDENTE' && <button onClick={() => editar(p)}>Editar</button>}{' '}
              {p.status === 'PENDENTE' && (perfil === 'CLIENTE'
                ? <button onClick={agir(() => api(`/pedidos/${p.id}`, { method: 'DELETE' }))}>Cancelar</button>
                : <><button onClick={avaliar(p.id, true)}>Aprovar</button> <button onClick={avaliar(p.id, false)}>Reprovar</button></>)}
              {p.status === 'APROVADO' && perfil !== 'CLIENTE' && <button onClick={contratar(p.id)}>Executar contrato</button>}
            </td></tr>))}
        </tbody></table>
    </div>)
}

const vazioCliente = { nome: '', cpf: '', rg: '', endereco: '', profissao: '' }

function Clientes({ perfil }) {
  const [lista, setLista] = useState([])
  const [f, setF] = useState(vazioCliente)
  const [rend, setRend] = useState([])
  const [editId, setEditId] = useState(null)
  const [aberto, setAberto] = useState(false)
  const [erro, setErro] = useState('')
  const carregar = () => api('/clientes').then(setLista).catch((e) => setErro(e.message))
  useEffect(() => { carregar() }, [])
  const set = (k) => (e) => setF({ ...f, [k]: e.target.value })
  const fechar = () => { setAberto(false); setEditId(null); setF(vazioCliente); setRend([]) }
  async function salvar() {
    try {
      await api(editId ? `/clientes/${editId}` : '/clientes', { method: editId ? 'PUT' : 'POST', body: { ...f, rendimentos: limpar(rend) } })
      setErro(''); fechar(); carregar()
    } catch (e) { setErro(e.message) }
  }
  const editar = (c) => { setEditId(c.id); setAberto(true); setF({ nome: c.nome ?? '', cpf: c.cpf ?? '', rg: c.rg ?? '', endereco: c.endereco ?? '', profissao: c.profissao ?? '' }); setRend((c.rendimentos ?? []).map((r) => ({ ...r }))) }
  async function excluir(c) {
    if (!confirm('Excluir cliente?')) return
    try { await api(`/clientes/${c.id}`, { method: 'DELETE' }); setErro(''); carregar() } catch (e) { setErro(e.message) }
  }
  return (
    <div><h2>Clientes</h2>
      {erro && <p style={{ color: 'crimson' }}>{erro}</p>}
      {perfil !== 'CLIENTE' && !aberto && <button onClick={() => setAberto(true)}>Novo cliente</button>}
      {aberto && (
        <div style={{ display: 'grid', gap: 6, maxWidth: 360, margin: '12px 0' }}>
          {campos.map((k) => <input key={k} placeholder={k} value={f[k]} onChange={set(k)} />)}
          <Rendimentos value={rend} onChange={setRend} />
          <div><button onClick={salvar}>Salvar</button> <button onClick={fechar}>Cancelar</button></div>
        </div>)}
      <table border="1" cellPadding="6"><thead><tr><th>Nome</th><th>CPF</th><th>RG</th><th>Endereço</th><th>Profissão</th><th>Rendimentos</th><th></th></tr></thead>
        <tbody>{lista.map((c) => <tr key={c.id}><td>{c.nome}</td><td>{c.cpf}</td><td>{c.rg}</td><td>{c.endereco}</td><td>{c.profissao}</td>
          <td>{(c.rendimentos ?? []).map((r) => `${r.entidadeEmpregadora}: ${r.valor}`).join(' | ')}</td>
          <td><button onClick={() => editar(c)}>Editar</button> <button onClick={() => excluir(c)}>Excluir</button></td></tr>)}</tbody></table>
    </div>)
}

export default function App() {
  const [perfil, setPerfil] = useState(localStorage.getItem('token') ? localStorage.getItem('perfil') : null)
  const [aba, setAba] = useState('pedidos')
  const sair = () => { localStorage.clear(); setPerfil(null) }
  if (!perfil) return <div style={{ padding: 24 }}><h1>Aluguel de Carros</h1><Auth onLogin={setPerfil} /></div>
  return (
    <div style={{ padding: 24 }}>
      <h1>Aluguel de Carros <small style={{ fontSize: 14 }}>({perfil})</small></h1>
      <nav style={{ display: 'flex', gap: 8, marginBottom: 16 }}>
        <button onClick={() => setAba('pedidos')}>Pedidos</button>
        <button onClick={() => setAba('clientes')}>Clientes</button>
        <button onClick={sair}>Sair</button>
      </nav>
      {aba === 'pedidos' ? <Pedidos perfil={perfil} /> : <Clientes perfil={perfil} />}
    </div>)
}
