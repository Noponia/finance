import React, {useState} from "react";

interface TaxReturnResponse {
  totalTax: number;
  taxRefund: number;
}

function App() {

  const [grossIncome, setGrossIncome] = useState(0);
  const [response, setResponse] = useState<TaxReturnResponse | null>(null);

  async function submit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();

    try {
      const res = await fetch('/api/v1/returns', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json'
        },
        body: JSON.stringify({
          'grossIncome': grossIncome,
          'taxPaid': 0.0,
          'year': '2024-25'
        })
      });

      if(!res.ok) {
        console.log(res.body);
      } else {
        const data = await res.json();
        setResponse(data);
      }
    } catch(error) {
      console.error(error);
    }
  }

  return (
    <>
      <form onSubmit={submit}>
        <input onChange={(e) => setGrossIncome(parseFloat(e.target.value))}></input>
        <button type="submit">Submit</button>
      </form>
      {
        response ? <p>Tax amount: {response.totalTax}</p> : <></>
      }
    </>
  );
}

export default App;
